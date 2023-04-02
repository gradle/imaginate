package confbuild

import conf.domain.ImageGenerator
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.Named
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

abstract class GeneratedImage(private val name: String) : Named, ImageInputs {

    override fun getName(): String = name

    init {
        prompt.convention("abstract neutral placeholder")
        width.convention(320)
        height.convention(width)
    }
}

interface ImageInputs {

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

@CacheableTask
abstract class ImageGeneratorTask : DefaultTask(), ImageInputs, ImageOutputs {

    @TaskAction
    fun action() {
        workers.noIsolation().submit(ImageGeneratorWork::class.java) {
            prompt.set(this@ImageGeneratorTask.prompt)
            width.set(this@ImageGeneratorTask.width)
            height.set(this@ImageGeneratorTask.height)
            image.set(this@ImageGeneratorTask.image)
        }
    }

    @get:Inject
    protected
    abstract val workers: WorkerExecutor
}

internal
abstract class ImageGeneratorParameters : WorkParameters, ImageInputs, ImageOutputs

internal
abstract class ImageGeneratorWork : WorkAction<ImageGeneratorParameters> {
    override fun execute(): Unit = runBlocking {
        parameters.apply {
            image.get().asFile.writeBytes(
                ImageGenerator().generate(prompt.get(), width.get(), height.get())
            )
        }
    }
}
