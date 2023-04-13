plugins {
    id("web-app")
}

dependencies {
    sharedBitmaps(projects.sharedResources)
}

kotlin {
    sourceSets {
        jsMain {
            dependencies {
                implementation(projects.sharedLogic)
                implementation(libs.imaginate.imageGeneration)
            }
        }
    }
}
