plugins {
    id("desktop-app")
}

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                implementation(projects.sharedLogic)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "imaginate.desktop.DesktopMainKt"
        nativeDistributions {
            linux {
                iconFile
            }
        }
    }
}
