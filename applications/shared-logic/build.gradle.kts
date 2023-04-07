plugins {
    id("shared-logic-library")
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
    namespace = "conf.shared.logic"
}
