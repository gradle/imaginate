plugins {
    alias(libs.plugins.kotlin.mpp)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "imaginate"

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
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain = getByName("commonMain") {
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
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        create("iosMain") {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}
