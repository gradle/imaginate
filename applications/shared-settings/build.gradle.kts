plugins {
    id("shared-settings-library")
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
