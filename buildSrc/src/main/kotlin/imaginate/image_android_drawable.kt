package imaginate

import com.android.ide.common.vectordrawable.Svg2Vector
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.submit
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

interface DrawAndroidImageInputs {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val vectorsDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty
}

@CacheableTask
abstract class DrawAndroidImage : DefaultTask(), DrawAndroidImageInputs {

    @get:Classpath
    internal
    abstract val workerClasspath: ConfigurableFileCollection

    @TaskAction
    fun action() {
        workers.classLoaderIsolation {
            classpath.from(workerClasspath)
        }.submit(DrawAndroidImageWork::class) {
            vectorsDirectory.set(this@DrawAndroidImage.vectorsDirectory)
            outputDirectory.set(this@DrawAndroidImage.outputDirectory)
        }
    }

    @get:Inject
    protected
    abstract val workers: WorkerExecutor
}

internal
abstract class DrawAndroidImageParameters : WorkParameters, DrawAndroidImageInputs

internal
abstract class DrawAndroidImageWork : WorkAction<DrawAndroidImageParameters> {

    override fun execute(): Unit = parameters.run {
        val vectorsDir = vectorsDirectory.get().asFile
        vectorsDir.walkTopDown()
            .filter { it.isFile }
            .forEach { svgFile ->
                val xmlFile = outputDirectory.get()
                    .dir("drawable-anydpi-v26")
                    .file(buildString {
                        append(
                            svgFile.parentFile
                                .relativeTo(vectorsDir)
                                .resolve(svgFile.nameWithoutExtension)
                        )
                        append(".xml")
                    })
                    .asFile
                xmlFile.parentFile.mkdirs()
                xmlFile.outputStream().use { output ->
                    val errors: String = try {
                        Svg2Vector.parseSvgToXml(svgFile, output)
                    } catch (e: Exception) {
                        throw Exception("Unable to parse svg file '$svgFile'", e)
                    }
                    if (xmlFile.length() == 0L) {
                        throw Exception("Unable to generate android drawable from svg file '$svgFile'")
                    }
                    if (errors.isNotEmpty()) {
                        throw Exception("Failed while generating android drawable in '$xmlFile'")
                    }
                }
            }
    }
}
