plugins {
    id("shared-resources")
}

generatedImages {
    register("icon") {
        prompt = "round icon that mixes Gradle and Kotlin brands with transparent background and space around it"
        width = 512
    }
    register("logo") {
        prompt = "hexagonal logo that mixes Gradle and Kotlin brands with transparent background and space around it"
        width = 512
    }
}
