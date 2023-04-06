package confbuild

import nu.studer.gradle.credentials.domain.CredentialsContainer
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.the

interface BuildCredentials {
    abstract val stableDiffusionApiKey: Property<String>
}

internal
val Project.buildCredentials: BuildCredentials
    get() = the()

internal
val CredentialsContainer.stableDiffusionBuildApiKey: String?
    get() {
        val apiKey = forKey("stableDiffusionBuildApiKey")
        if (apiKey == null) {
            println("""
            |
            |This build needs a Dream Studio API key.
            |None was provided, image generation will fallback to simple random images.
            |Get one from https://beta.dreamstudio.ai/account
            |
            |Then, run the following to store the API key: 
            |  ./gradlew addCredentials --key stableDiffusionBuildApiKey --value <YOUR_API_KEY>
            |
            |To remove the API key run:
            |  ./gradlew removeCredentials --key stableDiffusionBuildApiKey
            |
            """.trimMargin())
        }
        return apiKey
    }
