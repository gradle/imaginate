plugins {
    id("android-app")
}

val sharedIcon = configurations.register("sharedIcon") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(projects.sharedLogic)
    sharedIcon.name(project(":shared-resources", configuration = "sharedIcon"))
}

val generatedResDir = layout.buildDirectory.dir("generated-res")

val launcherIcon = tasks.register("launcherIcon", Sync::class) {
    from(sharedIcon)
    include("*.xml")
    into(generatedResDir.get().dir("drawable-anydpi-v26").asFile)
}

android {
    namespace = "imaginate.android"
    applicationVariants.all {
        registerGeneratedResFolders(files(generatedResDir) {
            builtBy(launcherIcon)
        })
    }
}
