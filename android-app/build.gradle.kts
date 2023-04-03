plugins {
    id("android-app")
}

val sharedIcon = configurations.register("sharedIcon") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(projects.sharedUi)
    sharedIcon.name(project(":shared-resources", configuration = "sharedIcon"))
}

val generatedResDir = layout.buildDirectory.dir("generated-res")

val launcherIcon = tasks.register("launcherIcon", Copy::class) {
    from(sharedIcon)
    include("*.xml")
    into(generatedResDir.get().dir("drawable-anydpi-v26").asFile)
    rename { "ic_launcher.xml" }
}

android {
    namespace = "conf.android"
    sourceSets {
        getByName("main") {
            res {
                srcDir(files(generatedResDir) {
                    builtBy(launcherIcon)
                })
            }
        }
    }
}

// TODO this is a workaround, fixme!
afterEvaluate {
    listOf(
        "processDebugResources", "mergeDebugResources", "mapDebugSourceSetPaths",
        "processReleaseResources", "mergeReleaseResources", "mapReleaseSourceSetPaths",
    ).forEach { taskName ->
        tasks.named(taskName) {
            dependsOn(launcherIcon)
        }
    }
}
