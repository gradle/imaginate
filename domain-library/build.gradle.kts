plugins {
    alias(libs.plugins.kotlin.mpp)
}

group = "conf"

kotlin {
    jvm {
        withJava()
        jvmToolchain(libs.versions.jvm.get().toInt())
        testRuns["test"].executionTask.configure {
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
    }
}
