import confbuild.androidCompileSdk
import confbuild.androidMinSdk
import confbuild.androidxActivityCompose
import confbuild.androidxAppcompat
import confbuild.androidxCoreKtx
import confbuild.jvm
import confbuild.libs

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
