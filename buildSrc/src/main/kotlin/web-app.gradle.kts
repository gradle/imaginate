plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("kotlin-compose-component")
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        named("jsMain") {
            dependencies {
                implementation(compose.html.core)
                implementation(compose.runtime)
            }
        }
    }
}
