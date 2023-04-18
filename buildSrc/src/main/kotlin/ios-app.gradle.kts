import imaginate.iosDeploymentTarget
import imaginate.libs

plugins {
    id("kotlin-ios-component")
    id("org.jetbrains.kotlin.native.cocoapods")
    id("kotlin-compose-component")
    id("shared-resources-consumer")
}

kotlin {

    cocoapods {
        version = project.version.toString()
        summary = project.name
        homepage = "https://github.com/gradle/imaginate"
        ios.deploymentTarget = libs.iosDeploymentTarget
        podfile = layout.projectDirectory.file("iosApp/Podfile").asFile
        framework {
            baseName = "imaginate-${project.name}"
            isStatic = true
        }
        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
    }

    sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
            }
        }
    }
}
