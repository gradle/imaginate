import confbuild.jvm
import confbuild.libs

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
