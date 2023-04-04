import confbuild.jvm
import confbuild.libs

plugins {
    id("kotlin-jvm-component")
    id("kotlin-compose-component")
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
