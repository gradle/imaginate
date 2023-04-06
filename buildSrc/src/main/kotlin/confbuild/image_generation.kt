package confbuild

import conf.domain.ImageGenerator
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.Named
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.submit
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

abstract class ImageSpec(private val name: String) : Named, ImageInputs {

    override fun getName(): String = name

    init {
        prompt.convention("abstract neutral placeholder")
        width.convention(320)
        height.convention(width)
    }
}

interface ImageInputs {

    @get:Input
    @get:Optional
    val apiKey: Property<String>

    @get:Input
    val prompt: Property<String>

    @get:Input
    val width: Property<Int>

    @get:Input
    val height: Property<Int>
}

interface ImageOutputs {
    @get:OutputFile
    abstract val image: RegularFileProperty
}

abstract class ImageGenerationSemaphore : BuildService<BuildServiceParameters.None>

@CacheableTask
abstract class GenerateImage : DefaultTask(), ImageInputs, ImageOutputs {

    @get:Classpath
    internal
    abstract val workerClasspath: ConfigurableFileCollection

    @TaskAction
    fun action() {
        workers.classLoaderIsolation {
            classpath.from(workerClasspath)
        }.submit(GenerateImageWork::class) {
            apiKey.set(this@GenerateImage.apiKey)
            prompt.set(this@GenerateImage.prompt)
            width.set(this@GenerateImage.width)
            height.set(this@GenerateImage.height)
            image.set(this@GenerateImage.image)
        }
    }

    @get:Inject
    protected
    abstract val workers: WorkerExecutor
}

internal
abstract class GenerateImageParameters : WorkParameters, ImageInputs, ImageOutputs

internal
abstract class GenerateImageWork : WorkAction<GenerateImageParameters> {
    override fun execute(): Unit = runBlocking {
        parameters.apply {
            image.get().asFile.writeBytes(
                ImageGenerator(apiKey.orNull).generate(prompt.get(), width.get(), height.get())
            )
        }
    }
}
