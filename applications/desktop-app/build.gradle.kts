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
            linux {
                iconFile = configurations.sharedBitmaps.flatMap { bitmaps ->
                    bitmaps.filter { it.name == "icon.jpg" }
                            .elements
                            .map { it.single().asFile }
                }
            }
            macOS {
                // TODO iconFile requires a .icns file
                bundleID = "org.gradle.imaginate"
            }
            windows {
                // TODO iconFile requires a .ico file
            }
        }
    }
}
