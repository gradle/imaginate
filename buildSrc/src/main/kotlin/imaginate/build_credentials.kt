package imaginate

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
    get() = forKey("stableDiffusionBuildApiKey")
