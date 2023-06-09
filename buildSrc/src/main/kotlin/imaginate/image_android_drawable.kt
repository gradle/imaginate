package imaginate

import com.android.ide.common.vectordrawable.Svg2Vector
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileType
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.submit
import org.gradle.work.ChangeType
import org.gradle.work.FileChange
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import java.io.File
import javax.inject.Inject

@CacheableTask
abstract class DrawAndroidImage : DefaultTask() {

    @get:Classpath
    internal
    abstract val workerClasspath: ConfigurableFileCollection

    @get:Incremental
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val vectorsDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Inject
    protected
    abstract val workers: WorkerExecutor

    @TaskAction
    fun action(inputChanges: InputChanges) =
        inputChanges.getFileChanges(vectorsDirectory).forEach { change ->
            when (change.fileType) {
                FileType.DIRECTORY -> return@forEach
                else -> onFileChange(change)
            }
        }

    private
    fun onFileChange(change: FileChange) =
        outputDirectory.get()
            .dir("drawable-anydpi-v26")
            .file(change.targetPathWithExtension(vectorsDirectory.get().asFile, "xml"))
            .asFile.let { targetFile ->

                when (change.changeType) {
                    ChangeType.REMOVED -> targetFile.delete()
                    else -> convert(change.file, targetFile)
                }
            }

    private
    fun convert(vectorFile: File, drawableFile: File) {
        logger.info("Converting '{}' to '{}'", vectorFile, drawableFile)
        workers.classLoaderIsolation {
            classpath.from(workerClasspath)
        }.submit(DrawAndroidImageWork::class) {
            vector.set(vectorFile)
            drawable.set(drawableFile)
        }
    }
}

internal
abstract class DrawAndroidImageParameters : WorkParameters {
    abstract val vector: Property<File>
    abstract val drawable: Property<File>
}

internal
abstract class DrawAndroidImageWork : WorkAction<DrawAndroidImageParameters> {

    override fun execute(): Unit = parameters.run {
        val xmlFile = drawable.get()
        val svgFile = vector.get()
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
