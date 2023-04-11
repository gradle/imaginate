plugins {
    id("android-library")
    id("kotlin-js-library")
    id("kotlin-jvm-component")
    id("kotlin-compose-component")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
            }
        }
    }
}
