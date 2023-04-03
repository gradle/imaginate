import confbuild.capitalized
import confbuild.Image
import confbuild.GenerateImage
import confbuild.VectorizeImage
import org.jetbrains.kotlin.gradle.utils.loadPropertyFromResources

plugins {
    id("base")
}

val imageTracerCoordinates: String
    get() = loadPropertyFromResources("tool-coordinates.properties", "imageTracer")

val imageTracerConfiguration = configurations.register("imageTracerClasspath")
dependencies {
    imageTracerConfiguration.name(imageTracerCoordinates)
}

val generatedImages = objects.domainObjectContainer(Image::class)
extensions.add("generatedImages", generatedImages)

val lifecycleTask = tasks.register("generateImages")

generatedImages.all {
    val inputs = this
    val baseTaskName = "${inputs.name.capitalized()}Image"
    val generation = tasks.register("generate$baseTaskName", GenerateImage::class) {
        prompt.set(inputs.prompt)
        width.set(inputs.width)
        height.set(inputs.height)
        image.set(layout.buildDirectory.file("generated-images/${inputs.name}.jpg"))
    }
    val vectorization = tasks.register("vectorize$baseTaskName", VectorizeImage::class) {
        imageTracerClasspath.from(imageTracerConfiguration)
        image.set(generation.flatMap { it.image })
        palleteSize.set(8)
        vector.set(layout.buildDirectory.file("generated-vectors/${inputs.name}.svg"))
    }
    lifecycleTask {
        dependsOn(vectorization)
    }
}
