import imaginate.buildCredentials
import imaginate.ImageFormat
import imaginate.imageGeneration
import imaginate.DrawAndroidImage
import imaginate.ImageGenerationSemaphore
import imaginate.ImageSpec
import imaginate.imageTracer
import imaginate.GenerateImages
import imaginate.libs
import imaginate.registerDependencyBucket
import imaginate.registerOutgoingImages
import imaginate.svg2vector
import imaginate.VectorizeImage

plugins {
    id("base")
    id("build-credentials")
}

// Custom DSL
val generatedImages = objects.domainObjectContainer(ImageSpec::class)
extensions.add("generatedImages", generatedImages)


// Image generation concurrently control
internal
val imageGenerationSemaphore = gradle.sharedServices.registerIfAbsent(
    "imageGenerationSemaphore",
    ImageGenerationSemaphore::class
) {
    maxParallelUsages = 1
}


// Published variants for consumption from other projects
val bitmapImages = configurations.registerOutgoingImages(
    "bitmapImages",
    objects.named(ImageFormat.BITMAP)
)
val drawableImages = configurations.registerOutgoingImages(
    "drawableImages",
    objects.named(ImageFormat.DRAWABLE)
)


// External tool dependencies
val imageGenerationClasspath = configurations.registerDependencyBucket("imageGenerationClasspath")
val imageTracerClasspath = configurations.registerDependencyBucket("imageTracerClasspath")
val svgToDrawableClasspath = configurations.registerDependencyBucket("svgToDrawableClasspath")
dependencies {
    imageGenerationClasspath(libs.imageGeneration)
    imageTracerClasspath(libs.imageTracer)
    svgToDrawableClasspath(libs.svg2vector)
}


// Custom tasks
val generateBitmaps = tasks.register("generateBitmaps", GenerateImages::class) {
    usesService(imageGenerationSemaphore)
    apiKey = buildCredentials.stableDiffusionApiKey
    workerClasspath.from(imageGenerationClasspath)
    outputDirectory = layout.projectDirectory.dir("src/images")
}
generatedImages.all {
    generateBitmaps {
        images.add(this@all)
    }
}
val generateVectors = tasks.register("generateVectors", VectorizeImage::class) {
    workerClasspath.from(imageTracerClasspath)
    bitmapsDirectory = generateBitmaps.flatMap { it.outputDirectory }
    palleteSize = 8
    outputDirectory = layout.buildDirectory.dir("generated-vectors")
}
val generateDrawables = tasks.register("generateDrawables", DrawAndroidImage::class) {
    workerClasspath.from(svgToDrawableClasspath)
    vectorsDirectory = generateVectors.flatMap { it.outputDirectory }
    outputDirectory = layout.buildDirectory.dir("generated-drawables")
}
tasks.register("generateImages") {
    dependsOn(generateDrawables)
}

// Publishing outputs to other projects
artifacts {
    add(bitmapImages.name, generateBitmaps.flatMap { it.outputDirectory })
    add(drawableImages.name, generateDrawables.flatMap { it.outputDirectory })
}
