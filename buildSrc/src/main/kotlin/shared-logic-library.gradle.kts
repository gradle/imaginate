import imaginate.androidCompileSdk
import imaginate.androidMinSdk
import imaginate.jvm
import imaginate.libs

plugins {
    id("com.android.library")
    id("kotlin-jvm-component")
    id("kotlin-compose-component")
}

kotlin {
    android()
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)
                implementation(compose.preview)
            }
        }
    }
}

android {

    compileSdk = libs.androidCompileSdk

    defaultConfig {
        minSdk = libs.androidMinSdk
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.jvm)
        targetCompatibility = JavaVersion.toVersion(libs.jvm)
    }
}
