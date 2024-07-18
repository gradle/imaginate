rootProject.name = "slides"

pluginManagement {
    repositories {
        maven {
            url = uri("https://repo.gradle.org/gradle/libs/")
            content {
                includeModule("org.ysb33r.gradle", "grolifant")
                includeModule("com.burgstaller", "okhttp-digest")
            }
        }
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    versionCatalogs {
        register("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
