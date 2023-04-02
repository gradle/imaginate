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

rootProject.name = "gradle-kotlinconf-2023-app"

includeBuild("domain-library")

include("shared-resources")
include("shared-ui")
include("desktop-app")
include("android-app")
include("web-app")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
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

