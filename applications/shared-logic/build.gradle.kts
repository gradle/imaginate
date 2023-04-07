plugins {
    id("shared-logic-library")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.imaginate.imageGeneration)
            }
        }
    }
}

android {
    namespace = "imaginate.shared.logic"
}
