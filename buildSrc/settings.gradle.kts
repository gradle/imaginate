plugins {
    id("org.gradle.toolchains.foojay-resolver-convention")
}

includeBuild("../domain-library")

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
