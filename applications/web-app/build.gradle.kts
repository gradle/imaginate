plugins {
    id("web-app")
}

kotlin {
    sourceSets {
        jsMain {
            dependencies {
                implementation(libs.imaginate.imageGeneration)
            }
        }
    }
}
