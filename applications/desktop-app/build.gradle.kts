plugins {
    id("desktop-app")
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
            linux {
                iconFile
            }
        }
    }
}

val sharedIcon = configurations.register("sharedIcon") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    sharedIcon.name(project(":shared-resources", configuration = "sharedIcon"))
}

val generatedResourcesDir = layout.buildDirectory.dir("generated-resources")

val icon = tasks.register("icon", Sync::class) {
    from(sharedIcon)
    include("*.jpg")
    into(generatedResourcesDir)
}

kotlin {
    sourceSets {
        jvmMain {
            resources.srcDir(files(generatedResourcesDir) {
                builtBy(icon)
            })
        }
    }
}
