plugins {
    id("shared-resources")
}

generatedImages {
    register("icon") {
        prompt = "capital letter K. elephant. pop art."
        width = 512
    }
    register("logo") {
        prompt = "elephant in the Kotlin island. caravaggio."
        width = 512
    }
}
