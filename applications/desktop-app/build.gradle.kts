plugins {
    id("desktop-app")
}

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                implementation(projects.sharedUi)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "conf.desktop.DesktopMainKt"
        nativeDistributions {
            linux {
                iconFile
            }
        }
    }
}
