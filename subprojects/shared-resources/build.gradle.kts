plugins {
    id("generated-images")
}

generatedImages {
    register("icon") {
        prompt = "icon that mixes Gradle and Kotlin brands"
        width = 256
    }
    register("splash") {
        prompt = "a steampunk splash screen for a fancy image generation application"
        width = 720
        height = 480
    }
}
