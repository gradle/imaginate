buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.gradle.toolchains:foojay-resolver:0.4.0")
    }
}

// Imperatively applying this is required in order to support Android Studio
apply(plugin = "org.gradle.toolchains.foojay-resolver-convention")

rootProject.name = "buildSrc"

includeBuild("../image-generation")

dependencyResolutionManagement {
    versionCatalogs {
        register("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}
