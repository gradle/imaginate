plugins {
    id("ios-app")
}

dependencies {
    sharedBitmaps(projects.sharedResources)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.sharedUi)
                implementation(projects.sharedLogic)
            }
        }
    }
}
