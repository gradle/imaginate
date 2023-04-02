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
}
