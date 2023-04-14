plugins {
    id("shared-resources")
}

generatedImages {
    register("icon") {
        prompt = "round icon. elephant. capital letter K. two colors."
        width = 512
    }
    register("logo") {
        prompt = "elephant in the Kotlin island. caravaggio."
        width = 512
    }
}
