package imaginate

import com.xcl.imagetracer_mod.ImageTracer
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileType
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
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
abstract class VectorizeImage : DefaultTask() {

    @get:Classpath
    internal
    abstract val workerClasspath: ConfigurableFileCollection

    @get:Incremental
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    internal
    abstract val bitmapsDirectory: DirectoryProperty

    @get:Input
    internal
    abstract val palleteSize: Property<Int>

    @get:OutputDirectory
    internal
    abstract val outputDirectory: DirectoryProperty

    @get:Inject
    protected
    abstract val workers: WorkerExecutor

    @TaskAction
    fun action(inputChanges: InputChanges) =
        inputChanges.getFileChanges(bitmapsDirectory).forEach { change ->
            when (change.fileType) {
                FileType.DIRECTORY -> return@forEach
                else -> onFileChange(change)
            }
        }

    private
    fun onFileChange(change: FileChange) =
        outputDirectory.get()
            .file(change.targetPathWithExtension(bitmapsDirectory.get().asFile, "svg"))
            .asFile.let { targetFile ->

                when (change.changeType) {
                    ChangeType.REMOVED -> targetFile.delete()
                    else -> convert(change.file, targetFile)
                }
            }

    private
    fun convert(bitmapFile: File, vectorFile: File) {
        project.logger.info("Converting '{}' to '{}'", bitmapFile, vectorFile)
        workers.classLoaderIsolation {
            classpath.from(workerClasspath)
        }.submit(ImageVectorizationWork::class) {
            bitmap.set(bitmapFile)
            palleteSize.set(this@VectorizeImage.palleteSize)
            vector.set(vectorFile)
        }
    }
}

internal
abstract class ImageVectorizationParameters : WorkParameters {
    abstract val bitmap: Property<File>
    abstract val palleteSize: Property<Int>
    abstract val vector: Property<File>
}

internal
abstract class ImageVectorizationWork : WorkAction<ImageVectorizationParameters> {

    override fun execute(): Unit = parameters.run {
        vector.get().let { svgFile ->
            svgFile.parentFile.mkdirs()
            svgFile.writeText(
                ImageTracer.imageToSVG(
                    bitmap.get().absolutePath,
                    options,
                    palette(palleteSize.get())
                )
            )
        }
    }

    private
    val options: HashMap<String, Float>
        get() = hashMapOf(

            // Tracing
            "ltres" to 1f,
            "qtres" to 1f,
            "pathomit" to 8f,

            // Color quantization
            "colorsampling" to 1f, // 1f means true ; 0f means false: starting with generated palette
            "numberofcolors" to 16f,
            "mincolorratio" to 0.02f,
            "colorquantcycles" to 3f,

            // SVG rendering
            "scale" to 1f,
            "roundcoords" to 1f, // 1f means rounded to 1 decimal places, like 7.3 ; 3f means rounded to 3 places, like 7.356 ; etc.
            "lcpr" to 0f,
            "qcpr" to 0f,
            "desc" to 0f, // 1f means true ; 0f means false: SVG descriptions deactivated
            "viewbox" to 0f, // 1f means true ; 0f means false: fixed width and height

            // Selective Gauss Blur
            "blurradius" to 4f, // 0f means deactivated; 1f .. 5f : blur with this radius
            "blurdelta" to 64f, // smaller than this RGB difference will be blurred
        )

    private
    fun palette(paleteSize: Int): Array<ByteArray> {
        if (paleteSize < 4 || paleteSize > 16) {
            throw IllegalArgumentException("Palette size must be between 4 and 16")
        }
        // signed byte values [ -128 .. 127 ] will be converted to [ 0 .. 255 ] in the svg
        val palette = Array(paleteSize) { ByteArray(4) }
        for (colorcnt in 0..(paleteSize - 1)) {
            palette[colorcnt][0] = (-128 + colorcnt * 32).toByte() // R
            palette[colorcnt][1] = (-128 + colorcnt * 32).toByte() // G
            palette[colorcnt][2] = (-128 + colorcnt * 32).toByte() // B
            palette[colorcnt][3] = 127.toByte() // A
        }
        return palette
    }
}

internal
fun FileChange.targetPathWithExtension(baseDir: File, extension: String): String = buildString {
    append(file.parentFile.relativeTo(baseDir).resolve(file.nameWithoutExtension))
    append(".")
    append(extension)
}
