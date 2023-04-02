plugins {
    alias(libs.plugins.kotlin.mpp)
    alias(libs.plugins.compose)
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        named("jsMain") {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.runtime)
                implementation("conf:domain-library")
                implementation(libs.ktor.utils)
            }
        }
    }
}
