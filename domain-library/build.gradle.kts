plugins {
    alias(libs.plugins.kotlin.mpp)
}

group = "conf"

kotlin {
    jvm {
        jvmToolchain(libs.versions.jvm.get().toInt())
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
}
