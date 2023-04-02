package confbuild

import conf.domain.ImageGenerator
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class ImageGeneratorTask : DefaultTask() {

    @get:OutputFile
    abstract val image: RegularFileProperty

    @TaskAction
    fun action() = runBlocking {
        image.get().asFile.writeBytes(ImageGenerator().generate("some"))
    }
}
