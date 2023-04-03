package confbuild

import com.android.ide.common.vectordrawable.Svg2Vector
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

interface DrawAndroidImageInputs {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val vector: RegularFileProperty

    @get:OutputFile
    abstract val drawable: RegularFileProperty
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
        }.submit(DrawAndroidImageWork::class.java) {
            vector.set(this@DrawAndroidImage.vector)
            drawable.set(this@DrawAndroidImage.drawable)
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
        val svgFile = vector.get().asFile
        val xmlFile = drawable.get().asFile
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
