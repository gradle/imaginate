plugins {
    id("shared-ui-library")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.sharedLogic)
                implementation(libs.imaginate.imageGeneration)
            }
        }
    }
}

android {
    namespace = "imaginate.shared.ui"
}
