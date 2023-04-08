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
        jsMain {
            resources.srcDir(files(generatedResourcesDir) {
                builtBy(icon)
            })
        }
    }
}
