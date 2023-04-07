plugins {
    alias(libs.plugins.kotlin.mpp)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "conf"

kotlin {
    jvm {
        withJava()
        jvmToolchain(libs.versions.jvm.get().toInt())
        testRuns["test"].executionTask {
            useJUnitPlatform()
        }
    }
    js(IR) {
        browser()
        binaries.library()
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                api(libs.coroutines.core)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }
        named("jvmMain") {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
        named("jsMain") {
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }
        named("jvmTest") {
            dependencies {
                implementation(libs.ktor.client.mock)
                implementation(kotlin("test"))
            }
        }
    }
}
