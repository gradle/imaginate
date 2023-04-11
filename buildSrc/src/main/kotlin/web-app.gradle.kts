plugins {
    id("kotlin-js-executable")
    id("kotlin-compose-component")
}

kotlin {
    sourceSets {
        named("jsMain") {
            dependencies {
                implementation(compose.html.core)
                implementation(compose.runtime)
            }
        }
    }
}
