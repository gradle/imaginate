plugins {
    id("shared-ui-library")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.conf.domainLibrary)
            }
        }
    }
}

android {
    namespace = "conf.shared.ui"
}
