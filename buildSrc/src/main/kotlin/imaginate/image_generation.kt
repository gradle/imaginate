package imaginate

import imaginate.generation.ImageGenerator
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.Named
import org.gradle.api.attributes.Attribute
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.submit
import org.gradle.work.DisableCachingByDefault
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import java.io.Serializable
import javax.inject.Inject


interface ImageFormat : Named {
    companion object {

        val IMAGE_FORMAT_ATTRIBUTE: Attribute<ImageFormat> =
            Attribute.of("imageFormat", ImageFormat::class.java)

        val BITMAP = "bitmap"
        val DRAWABLE = "drawable"
    }
}

abstract class ImageGenerationSemaphore : BuildService<BuildServiceParameters.None>

abstract class ImageSpec(private val name: String) : Named {

    @Input
    override fun getName(): String = name

    @get:Input
    abstract val prompt: Property<String>

    @get:Input
    abstract val width: Property<Int>

    @get:Input
    abstract val height: Property<Int>

    init {
        prompt.convention("abstract neutral placeholder")
        width.convention(320)
        height.convention(width)
    }
}

interface ImageInputs {

    @get:Input
    @get:Optional
    abstract val apiKey: Property<String>

    @get:Nested
    val images: ListProperty<ImageSpec>
}

interface ImageOutputs {
    @get:OutputDirectory
    val outputDirectory: DirectoryProperty
}

@DisableCachingByDefault(because = "Outputs are commited to git")
abstract class GenerateImage : DefaultTask(), ImageInputs, ImageOutputs {

    @get:Classpath
    internal
    abstract val workerClasspath: ConfigurableFileCollection

    init {
        outputs.upToDateWhen {
            outputDirectory.get().asFile.isFile
        }
    }

    @TaskAction
    fun action() {
        workers.classLoaderIsolation {
            classpath.from(workerClasspath)
        }.submit(GenerateImageWork::class) {
            apiKey.set(this@GenerateImage.apiKey)
            images.set(
                this@GenerateImage.images.get().map {
                    ImageSpecIsolate(
                        it.name,
                        it.prompt.get(),
                        it.width.get(),
                        it.height.get()
                    )
                })
            outputDirectory.set(this@GenerateImage.outputDirectory)
        }
    }

    @get:Inject
    protected
    abstract val workers: WorkerExecutor
}

internal
abstract class GenerateImageParameters : WorkParameters, ImageOutputs {
    abstract val apiKey: Property<String>
    abstract val images: ListProperty<ImageSpecIsolate>
}

internal
data class ImageSpecIsolate(
    val name: String,
    val prompt: String,
    val width: Int,
    val height: Int
) : Serializable

internal
abstract class GenerateImageWork : WorkAction<GenerateImageParameters> {
    override fun execute(): Unit = runBlocking {
        parameters.apply {
            val outputDir = outputDirectory.get()
            images.get().forEach { image ->
                val bitmapFile = outputDir.file("${image.name}.jpg").asFile
                bitmapFile.parentFile.mkdirs()
                bitmapFile.writeBytes(
                    ImageGenerator(apiKey.orNull).generate(
                        image.prompt,
                        image.width,
                        image.height
                    )
                )
            }
        }
    }
}
