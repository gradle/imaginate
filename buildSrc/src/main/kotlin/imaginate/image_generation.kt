package imaginate

import imaginate.generation.ImageGenerator
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.Named
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.submit
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import java.io.Serializable
import javax.inject.Inject

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

internal
abstract class ImageGenerationSemaphore : BuildService<BuildServiceParameters.None>

@CacheableTask
abstract class GenerateImages : DefaultTask() {

    @get:Classpath
    internal
    abstract val workerClasspath: ConfigurableFileCollection

    @get:Input
    @get:Optional
    internal
    abstract val apiKey: Property<String>

    @get:Nested
    internal
    abstract val images: ListProperty<ImageSpec>

    @get:OutputDirectory
    internal
    abstract val outputDirectory: DirectoryProperty

    @get:Inject
    protected
    abstract val workers: WorkerExecutor

    @TaskAction
    fun generate() {
        workers.classLoaderIsolation {
            classpath.from(workerClasspath)
        }.submit(GenerateImageWork::class) {
            apiKey.set(this@GenerateImages.apiKey)
            images.set(this@GenerateImages.images.get().map { ImageSpecIsolate(it) })
            outputDirectory.set(this@GenerateImages.outputDirectory)
        }
    }
}

internal
abstract class GenerateImageParameters : WorkParameters {
    abstract val apiKey: Property<String>
    abstract val images: ListProperty<ImageSpecIsolate>
    abstract val outputDirectory: DirectoryProperty
}

internal
data class ImageSpecIsolate(
        val name: String,
        val prompt: String,
        val width: Int,
        val height: Int
) : Serializable {

    constructor(spec: ImageSpec) :
            this(spec.name, spec.prompt.get(), spec.width.get(), spec.height.get())
}

internal
abstract class GenerateImageWork : WorkAction<GenerateImageParameters> {

    override fun execute(): Unit = runBlocking {
        parameters.apply {
            val imageGenerator by lazy {
                ImageGenerator(validApiKey().orNull)
            }
            val outputDir = outputDirectory.get()
            images.get().forEach { image ->
                outputDir.file(image.bitmapFileName).asFile.run {
                    if (!isFile) {
                        parentFile.mkdirs()
                        writeBytes(imageGenerator.generate(image))
                    }
                }
            }
        }
    }

    private
    val ImageSpecIsolate.bitmapFileName
        get() = "$name.jpg"

    private
    suspend fun ImageGenerator.generate(spec: ImageSpecIsolate): ByteArray =
            generate(
                    spec.prompt,
                    spec.width,
                    spec.height
            ).let { result ->
                when (result) {
                    is ImageGenerator.Result.Success -> result.image
                    is ImageGenerator.Result.Failure -> throw Exception(result.reason)
                }
            }

    private
    fun validApiKey(): Property<String> {
        if (!parameters.apiKey.isPresent) {
            println(
                    """
                |
                |This build needs a Dream Studio API key.
                |None was provided, image generation will fallback to simple random images.
                |Get one from https://beta.dreamstudio.ai/account
                |
                |Then, run the following to store the API key: 
                |  ./gradlew addCredentials --key stableDiffusionBuildApiKey --value <YOUR_API_KEY>
                |
                |To remove the API key run:
                |  ./gradlew removeCredentials --key stableDiffusionBuildApiKey
                |
                """.trimMargin()
            )
        }
        return parameters.apiKey
    }
}
