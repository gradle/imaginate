plugins {
    id("desktop-app")
}

dependencies {
    sharedBitmaps(projects.sharedResources)
}

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                implementation(projects.sharedUi)
                implementation(projects.sharedLogic)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "imaginate.desktop.DesktopMainKt"
        nativeDistributions {
            packageName = "imaginate"
        }
    }
}
