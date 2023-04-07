plugins {
    id("shared-ui-library")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.conf.imageGeneration)
            }
        }
    }
}

android {
    namespace = "conf.shared.ui"
}
