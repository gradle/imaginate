plugins {
    id("android-app")
}

android {
    namespace = "conf.android"
}

dependencies {
    implementation(projects.sharedUi)
}
