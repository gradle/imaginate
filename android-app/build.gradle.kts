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

val launcherIcon = tasks.register("launcherIcon") {
    doLast {
        sharedIcon.get().files.single { it.extension == "xml" }.copyTo(
            generatedResDir.get().file("drawable-anydpi-v26/ic_launcher.xml").asFile
        )
    }
}

android {
    namespace = "conf.android"
    sourceSets {
        named("main") {
            res {
                srcDir(files(generatedResDir) {
                    builtBy(launcherIcon)
                })
            }
        }
    }
}
