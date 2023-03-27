plugins {
    alias(libs.plugins.kotlin.mpp)
    alias(libs.plugins.compose)
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
        named("jvmMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":common-logic"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "conf.desktop.DesktopMainKt"
    }
}
