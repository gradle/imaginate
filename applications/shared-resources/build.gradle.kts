plugins {
    id("shared-resources")
}

generatedImages {
    register("icon") {
        prompt = "round logo that mixes Gradle and Kotlin brands with transparent background and space around it"
        width = 512
    }
}
