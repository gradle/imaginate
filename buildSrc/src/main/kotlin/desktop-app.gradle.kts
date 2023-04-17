plugins {
    id("kotlin-jvm-component")
    id("kotlin-compose-component")
    id("shared-resources-consumer")
}

kotlin {
    sourceSets {
        named("jvmMain") {
            resources.srcDir(files(tasks.named("sharedBitmaps")))
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}
