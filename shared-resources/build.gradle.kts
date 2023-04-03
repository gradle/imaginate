plugins {
    id("generated-images")
}

generatedImages {
    register("icon") {
        prompt.set("icon that mixes Gradle and Kotlin brands")
        width.set(256)
    }
    register("splash") {
        prompt.set("a steampunk splash screen for a fancy image generation application")
        width.set(720)
        height.set(480)
    }
}
