plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.mpp)
    alias(libs.plugins.compose)
}

kotlin {

    jvm {
        jvmToolchain(libs.versions.jvm.get().toInt())
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    android()

    sourceSets {
        named("commonMain") {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation(projects.sharedLogic)
                implementation(compose.preview) {
                    because("Needed only for preview")
                }
            }
        }
        named("androidMain") {
            dependencies {
                api(libs.androidx.appcompat)
                api(libs.androidx.core.ktx)
            }
        }
    }
}

android {

    namespace = "conf.shared.ui"

    compileSdk = libs.versions.android.sdk.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.sdk.min.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
    }
}
