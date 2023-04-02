import confbuild.capitalized
import confbuild.GeneratedImage
import confbuild.ImageGeneratorTask

plugins {
    id("base")
}

val generatedImages = objects.domainObjectContainer(GeneratedImage::class)
extensions.add("generatedImages", generatedImages)

val lifecycleTask = tasks.register("generateImages")

generatedImages.all {
    val inputs = this
    val taskName = "generate${inputs.name.capitalized()}Image"
    val task = tasks.register(taskName, ImageGeneratorTask::class) {
        prompt.set(inputs.prompt)
        width.set(inputs.width)
        height.set(inputs.height)
        image.set(layout.buildDirectory.file("generated-images/${inputs.name}.jpg"))
    }
    lifecycleTask {
        dependsOn(task)
    }
}
