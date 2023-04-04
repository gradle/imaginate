import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

plugins {
    id("base")
}

plugins.withType<YarnPlugin> {
    configure<YarnRootExtension> {
        lockFileDirectory = layout.projectDirectory.dir("gradle/kotlin-js-store").asFile
    }
}
