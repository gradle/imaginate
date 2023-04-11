plugins {
    id("kotlin-js-executable")
    id("kotlin-compose-component")
    id("shared-resources-consumer")
}

kotlin {
    sourceSets {
        named("jsMain") {
            resources.srcDir(files(tasks.named("sharedBitmaps")))
            dependencies {
                implementation(compose.html.core)
                implementation(compose.runtime)
            }
        }
    }
}
