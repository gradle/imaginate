import imaginate.buildCredentials
import imaginate.ImageFormat
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

// External tool dependencies
val imageGenerationClasspath = configurations.register("imageGenerationClasspath") {
    isCanBeResolved = true
    isCanBeConsumed = false
}
val imageTracerClasspath = configurations.register("imageTracerClasspath") {
    isCanBeResolved = true
    isCanBeConsumed = false
}
val svgToDrawableClasspath = configurations.register("svgToDrawableClasspath") {
    isCanBeResolved = true
    isCanBeConsumed = false
}
dependencies {
    imageGenerationClasspath.name(libs.imageGeneration)
    imageTracerClasspath.name(libs.imageTracer)
    svgToDrawableClasspath.name(libs.svg2vector)
}

// Image generation concurrently control
val imageGenerationSemaphore = gradle.sharedServices.registerIfAbsent(
    "imageGenerationSemaphore",
    ImageGenerationSemaphore::class
) {
    maxParallelUsages = 1
}

// Published variants for consumption from other projects
val bitmapImages = configurations.create("bitmapImages") {
    isCanBeConsumed = true
    isCanBeResolved = false
    attributes {
        attribute(ImageFormat.IMAGE_FORMAT_ATTRIBUTE, objects.named(ImageFormat.BITMAP))
    }
}
val drawableImages = configurations.create("drawableImages") {
    isCanBeConsumed = true
    isCanBeResolved = false
    attributes {
        attribute(ImageFormat.IMAGE_FORMAT_ATTRIBUTE, objects.named(ImageFormat.DRAWABLE))
    }
}

// Custom DSL
val generatedImages = objects.domainObjectContainer(ImageSpec::class)
extensions.add("generatedImages", generatedImages)

// Custom tasks
val lifecycleTask = tasks.register("generateImages")
generatedImages.all {
    val inputs = this
    val baseTaskName = "${inputs.name.capitalized()}Image"
    val generation = tasks.register("generate$baseTaskName", GenerateImage::class) {
        usesService(imageGenerationSemaphore)
        apiKey = buildCredentials.stableDiffusionApiKey
        workerClasspath.from(imageGenerationClasspath)
        prompt = inputs.prompt
        width = inputs.width
        height = inputs.height
        image = layout.projectDirectory.file("src/images/${inputs.name}.jpg")
    }
    val vectorization = tasks.register("vectorize$baseTaskName", VectorizeImage::class) {
        workerClasspath.from(imageTracerClasspath)
        image = generation.flatMap { it.image }
        palleteSize = 8
        vector = layout.buildDirectory.file("generated-vectors/${inputs.name}.svg")
    }
    val drawable = tasks.register("draw$baseTaskName", DrawAndroidImage::class) {
        workerClasspath.from(svgToDrawableClasspath)
        vector = vectorization.flatMap { it.vector }
        outputDirectory = layout.buildDirectory.dir("generated-drawables")
    }
    lifecycleTask {
        dependsOn(drawable)
    }

    // Publishing outputs to other projects
    artifacts {
        add(bitmapImages.name, generation.flatMap { it.image })
        add(drawableImages.name, drawable.flatMap { it.outputDirectory })
    }
}
