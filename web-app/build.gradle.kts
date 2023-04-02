plugins {
    id("web-app")
}

kotlin {
    sourceSets {
        jsMain {
            dependencies {
                implementation(libs.conf.domainLibrary)
                implementation(libs.ktor.utils)
            }
        }
    }
}
