plugins {
    id("shared-ui-library")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.sharedLogic)
                implementation(compose.material3)
                implementation(libs.imaginate.imageGeneration)
            }
        }
    }
}

android {
    namespace = "imaginate.shared.ui"
}
