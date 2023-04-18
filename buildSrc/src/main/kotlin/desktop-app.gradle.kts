import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Rpm

plugins {
    id("kotlin-jvm-component")
    id("kotlin-compose-component")
    id("shared-resources-consumer")
}

kotlin {
    sourceSets {
        named("jvmMain") {
            resources.srcDir(files(tasks.named("sharedBitmaps")))
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.desktop {
    application {
        nativeDistributions {
            targetFormats(Deb, Rpm, Dmg, Msi)
        }
    }
}
