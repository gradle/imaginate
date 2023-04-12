plugins {
    id("android-app")
}

dependencies {
    sharedDrawables(projects.sharedResources)
}

dependencies {
    implementation(projects.sharedUi)
    implementation(projects.sharedLogic)
}

android {
    namespace = "imaginate.android"
}
