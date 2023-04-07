import imaginate.androidCompileSdk
import imaginate.androidMinSdk
import imaginate.androidxActivityCompose
import imaginate.androidxAppcompat
import imaginate.androidxCoreKtx
import imaginate.jvm
import imaginate.libs

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-compose-component")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.jvm)
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

dependencies {
    implementation(libs.androidxAppcompat)
    implementation(libs.androidxCoreKtx)
    implementation(libs.androidxActivityCompose)
}
