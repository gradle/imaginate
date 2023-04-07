import imaginate.buildCredentials
import imaginate.imageGeneration
import imaginate.DrawAndroidImage
import imaginate.capitalized
import imaginate.ImageGenerationSemaphore
import imaginate.ImageSpec
import imaginate.imageTracer
import imaginate.GenerateImage
import imaginate.libs
import imaginate.svg2vector
import imaginate.VectorizeImage

plugins {
    id("base")
    id("build-credentials")
}

val imageGenerationConfiguration = configurations.register("imageGenerationClasspath") {
    isCanBeResolved = true
    isCanBeConsumed = false
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
    imageGenerationConfiguration.name(libs.imageGeneration)
    imageTracerConfiguration.name(libs.imageTracer)
    svgToDrawableConfiguration.name(libs.svg2vector)
}

val generatedImages = objects.domainObjectContainer(ImageSpec::class)
extensions.add("generatedImages", generatedImages)

val lifecycleTask = tasks.register("generateImages")

val imageGenerationSemaphore = gradle.sharedServices.registerIfAbsent(
    "imageGenerationSemaphore",
    ImageGenerationSemaphore::class
) {
    maxParallelUsages = 1
}

generatedImages.all {
    val inputs = this
    val baseTaskName = "${inputs.name.capitalized()}Image"
    val generation = tasks.register("generate$baseTaskName", GenerateImage::class) {
        usesService(imageGenerationSemaphore)
        apiKey = buildCredentials.stableDiffusionApiKey
        workerClasspath.from(imageGenerationConfiguration)
        prompt = inputs.prompt
        width = inputs.width
        height = inputs.height
        image = layout.projectDirectory.file("src/images/${inputs.name}.jpg")
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
