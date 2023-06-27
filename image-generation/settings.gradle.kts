plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "image-generation"

dependencyResolutionManagement {
    versionCatalogs {
        register("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
    repositories {
        mavenCentral()
    }
}
