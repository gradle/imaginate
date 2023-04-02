plugins {
    alias(libs.plugins.kotlin.mpp)
    id("domain-plugin")
}

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
        getByName("commonMain") {
            dependencies {
                implementation("conf:domain-library")
            }
        }
    }
}
