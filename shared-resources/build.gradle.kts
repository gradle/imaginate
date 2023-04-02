plugins {
    id("image-generator")
}

generatedImages {
    register("icon") {
        prompt.set("icon that mixes Gradle and Kotlin brands")
        width.set(64)
    }
    register("splash") {
        prompt.set("a steampunk splash screen for a fancy image generation application")
        width.set(640)
        height.set(480)
    }
}
