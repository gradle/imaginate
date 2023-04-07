import imaginate.jvm
import imaginate.libs

plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    jvm {
        jvmToolchain(libs.jvm)
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
}
