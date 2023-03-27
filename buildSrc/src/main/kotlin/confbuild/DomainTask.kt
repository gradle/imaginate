package confbuild

import conf.domain.domainFunction
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class DomainTask : DefaultTask() {

    @get:Input
    abstract val input: Property<String>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun action() {
        output.get().asFile.writeText(domainFunction(input.get()))
    }
}