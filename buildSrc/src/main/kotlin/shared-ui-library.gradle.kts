plugins {
    id("android-library")
    id("kotlin-jvm-component")
    id("kotlin-compose-component")
}

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)
                implementation(compose.preview)
            }
        }
    }
}
