import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

plugins {
    id("base")
}

plugins.withType(YarnPlugin::class) {
    configure<YarnRootExtension> {
        download = System.getenv("CI") != "true"
        lockFileDirectory = layout.projectDirectory.dir("gradle/kotlin-js-store").asFile
    }
}
