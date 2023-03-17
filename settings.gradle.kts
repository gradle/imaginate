pluginManagement {
    includeBuild("build-logic")
}

includeBuild("domain-library")

include("common-logic")
include("desktop-app")
include("android-app")
include("web-app")

