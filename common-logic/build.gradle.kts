plugins {
    alias(libs.plugins.kotlin.mpp)
    id("domain-plugin")
}

kotlin {
    jvm {
        jvmToolchain(libs.versions.jvm.get().toInt())
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation("conf:domain-library")
            }
        }
    }
}
