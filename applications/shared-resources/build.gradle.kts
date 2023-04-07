plugins {
    id("generated-images")
}

generatedImages {
    register("icon") {
        prompt = "round logo that mixes Gradle and Kotlin brands with transparent background and space around it"
        width = 512
    }
    register("splash") {
        prompt = "complex steampunk machinery in an abstract style with a wooden frame"
        width = 512
    }
}
