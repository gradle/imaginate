package imaginate

import com.xcl.imagetracer_mod.ImageTracer
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
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
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

interface ImageVectorizationInputs {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val bitmapsDirectory: DirectoryProperty

    @get:Input
    abstract val palleteSize: Property<Int>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty
}

@CacheableTask
abstract class VectorizeImage : DefaultTask(), ImageVectorizationInputs {

    @get:Classpath
    internal
    abstract val workerClasspath: ConfigurableFileCollection

    @TaskAction
    fun action() {
        workers.classLoaderIsolation {
            classpath.from(workerClasspath)
        }.submit(ImageVectorizationWork::class) {
            bitmapsDirectory.set(this@VectorizeImage.bitmapsDirectory)
            palleteSize.set(this@VectorizeImage.palleteSize)
            outputDirectory.set(this@VectorizeImage.outputDirectory)
        }
    }

    @get:Inject
    protected
    abstract val workers: WorkerExecutor
}

internal
abstract class ImageVectorizationParameters : WorkParameters, ImageVectorizationInputs

internal
abstract class ImageVectorizationWork : WorkAction<ImageVectorizationParameters> {

    override fun execute(): Unit = parameters.run {
        val imagesDir = bitmapsDirectory.get().asFile
        val outputDir = outputDirectory.get()
        imagesDir.walkTopDown()
            .filter { it.isFile }
            .forEach { bitmapFile ->
                val svgFile = outputDir.file(buildString {
                    append(
                        bitmapFile.parentFile
                            .relativeTo(imagesDir)
                            .resolve(bitmapFile.nameWithoutExtension)
                    )
                    append(".svg")
                }).asFile
                svgFile.parentFile.mkdirs()
                svgFile.writeText(
                    ImageTracer.imageToSVG(
                        bitmapFile.absolutePath,
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