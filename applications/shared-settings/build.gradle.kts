plugins {
    id("shared-logic-library")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.multiplatform.settings)
            }
        }
    }
}

android {
    namespace = "imaginate.shared.settings"
}
