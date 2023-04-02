plugins {
    alias(libs.plugins.kotlin.mpp)
    alias(libs.plugins.compose)
    id("domain-plugin")
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
                implementation(projects.sharedLogic)
                implementation(libs.ktor.utils)
            }
        }
    }
}
