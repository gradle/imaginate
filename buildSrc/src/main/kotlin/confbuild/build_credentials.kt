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
val CredentialsContainer.stableDiffusionBuildApiKey: String
    get() = requireNotNull(forKey("stableDiffusionBuildApiKey")) {
        """
            |This build requires a Stable Diffusion API key.
            |Get one from https://stablediffusionapi.com/settings/api
            |
            |Then, run the following to store the API key: 
            |  ./gradlew addCredentials --key stableDiffusionBuildApiKey --value <YOUR_API_KEY>
            |
            |To remove the API key run:
            |  ./gradlew removeCredentials --key someKey
        """.trimMargin()
    }
