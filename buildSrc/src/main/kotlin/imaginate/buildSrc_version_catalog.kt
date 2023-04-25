package imaginate

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.the

internal
val Project.libs: VersionCatalog
    get() = the<VersionCatalogsExtension>().named("libs")

internal
val VersionCatalog.jvm: Int
    get() = findVersion("jvm").get().requiredVersion.toInt()

internal
val VersionCatalog.iosDeploymentTarget: String
    get() = findVersion("ios-deploymentTarget").get().requiredVersion

internal
val VersionCatalog.androidCompileSdk: Int
    get() = findVersion("android-sdk-compile").get().requiredVersion.toInt()

internal
val VersionCatalog.androidMinSdk: Int
    get() = findVersion("android-sdk-min").get().requiredVersion.toInt()

internal
val VersionCatalog.androidxAppcompat: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("androidx-appcompat").get()

internal
val VersionCatalog.androidxCoreKtx: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("androidx-core-ktx").get()

internal
val VersionCatalog.androidxActivityCompose: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("androidx-activity-compose").get()

internal
val VersionCatalog.imageGeneration: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("imaginate.imageGeneration").get()

internal
val VersionCatalog.imageTracer: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("imageTracer").get()

internal
val VersionCatalog.svg2vector: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("svg2vector").get()
