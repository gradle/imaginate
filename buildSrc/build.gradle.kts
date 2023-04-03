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
    compileOnly(libs.svg2vector)

    implementation(libs.conf.domainLibrary)
    implementation(libs.plugins.kotlin.mpp)
    implementation(libs.plugins.kotlin.android)
    implementation(libs.plugins.android.application)
    implementation(libs.plugins.android.library)
    implementation(libs.plugins.compose)
}

fun DependencyHandler.implementation(pluginDependency: Provider<PluginDependency>): Dependency? =
    add("implementation", pluginDependency.map {
        "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version.requiredVersion}"
    }.get())
