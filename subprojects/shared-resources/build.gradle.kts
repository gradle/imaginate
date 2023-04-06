plugins {
    id("generated-images")
}

generatedImages {
    register("icon") {
        prompt = "abstract logo that mixes Gradle and Kotlin brands with transparent background"
        width = 512
    }
    register("splash") {
        prompt = "complex machinery in a steampunk style"
        width = 512
    }
}
