import imaginate.jvm
import imaginate.libs

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
