import confbuild.DrawAndroidImage
import confbuild.capitalized
import confbuild.Image
import confbuild.imageTracer
import confbuild.GenerateImage
import confbuild.libs
import confbuild.svg2vector
import confbuild.VectorizeImage

plugins {
    id("base")
}

val imageTracerConfiguration = configurations.register("imageTracerClasspath") {
    isCanBeResolved = true
    isCanBeConsumed = false
}
val svgToDrawableConfiguration = configurations.register("svgToDrawableClasspath") {
    isCanBeResolved = true
    isCanBeConsumed = false
}
dependencies {
    imageTracerConfiguration.name(libs.imageTracer)
    svgToDrawableConfiguration.name(libs.svg2vector)
}

val generatedImages = objects.domainObjectContainer(Image::class)
extensions.add("generatedImages", generatedImages)

val lifecycleTask = tasks.register("generateImages")

generatedImages.all {
    val inputs = this
    val baseTaskName = "${inputs.name.capitalized()}Image"
    val generation = tasks.register("generate$baseTaskName", GenerateImage::class) {
        prompt = inputs.prompt
        width = inputs.width
        height = inputs.height
        image = layout.buildDirectory.file("generated-images/${inputs.name}.jpg")
    }
    val vectorization = tasks.register("vectorize$baseTaskName", VectorizeImage::class) {
        workerClasspath.from(imageTracerConfiguration)
        image = generation.flatMap { it.image }
        palleteSize = 8
        vector = layout.buildDirectory.file("generated-vectors/${inputs.name}.svg")
    }
    val drawable = tasks.register("draw$baseTaskName", DrawAndroidImage::class) {
        workerClasspath.from(svgToDrawableConfiguration)
        vector = vectorization.flatMap { it.vector }
        drawable = layout.buildDirectory.file("generated-drawables/${inputs.name}.xml")
    }
    lifecycleTask {
        dependsOn(drawable)
    }

    // Sharing outputs to other projects
    val sharedConfiguration = configurations.create("shared${inputs.name.capitalized()}") {
        isCanBeConsumed = true
        isCanBeResolved = false
    }
    artifacts {
        add(sharedConfiguration.name, generation.flatMap { it.image })
        add(sharedConfiguration.name, vectorization.flatMap { it.vector })
        add(sharedConfiguration.name, drawable.flatMap { it.drawable })
    }
}
