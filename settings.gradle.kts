pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
    }
}

plugins {
    `gradle-enterprise`
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

rootProject.name = "imaginate"

includeBuild("image-generation")
includeBuild("slides")

include("shared-resources")
include("shared-settings")
include("shared-logic")
include("desktop-app")
include("android-app")
include("web-app")

for (project in rootProject.children) {
    project.projectDir = file("applications/${project.name}")
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://androidx.dev/storage/compose-compiler/repository/")
    }
}

val isCI = System.getenv("CI") == "true"
if (isCI) {
    gradleEnterprise {
        buildScan {
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
            publishAlways()
            tag("CI")
        }
    }
}

