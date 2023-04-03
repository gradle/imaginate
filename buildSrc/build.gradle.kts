plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvm.get().toInt()))
    }
}

dependencies {

    compileOnly(libs.imageTracer)

    implementation(libs.conf.domainLibrary)
    implementation(libs.plugins.kotlin.mpp)
    implementation(libs.plugins.kotlin.android)
    implementation(libs.plugins.android.application)
    implementation(libs.plugins.android.library)
    implementation(libs.plugins.compose)
}

val toolCoordinatesDir = layout.buildDirectory.dir("tool-coordinates")
val toolCoordinates = tasks.register("toolVersions", WriteProperties::class) {
    property("imageTracer", libs.imageTracer.get().toString())
    destinationFile.set(toolCoordinatesDir.map { it.file("tool-coordinates.properties") })
}

sourceSets {
    main {
        resources.srcDir(files(toolCoordinatesDir) { builtBy(toolCoordinates) })
    }
}

fun DependencyHandler.implementation(pluginDependency: Provider<PluginDependency>): Dependency? =
    add(
        "implementation",
        pluginDependency.map {
            "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version.requiredVersion}"
        }.get()
    )
