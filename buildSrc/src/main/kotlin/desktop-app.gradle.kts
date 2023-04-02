import confbuild.jvm
import confbuild.libs

plugins {
    id("kotlin-jvm-component")
    id("org.jetbrains.compose")
}

kotlin {
    sourceSets {
        named("jvmMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}
