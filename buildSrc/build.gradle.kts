plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.jvm.get().toInt())
    }
}

dependencies {

    compileOnly(libs.imaginate.imageGeneration)
    compileOnly(libs.imageTracer)
    compileOnly(libs.svg2vector)

    implementation(libs.plugins.kotlin.mpp)
    implementation(libs.plugins.kotlin.android)
    implementation(libs.plugins.kotlin.native.cocoapods)
    implementation(libs.plugins.android.application)
    implementation(libs.plugins.android.library)
    implementation(libs.plugins.compose)
    implementation(libs.plugins.credentials)
}

// This is a future Gradle feature to make declaring dependencies to plugins simpler
// See an upvote https://docs.google.com/document/d/1P7aTeeVNhkhwxcS5sQNFrSsmqJOhDo3G8kUdhtp_vyM
fun DependencyHandler.implementation(pluginDependency: Provider<PluginDependency>): Dependency? =
    add("implementation", pluginDependency.map {
        "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version.requiredVersion}"
    }.get())
