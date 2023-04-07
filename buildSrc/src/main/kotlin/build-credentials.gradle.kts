import nu.studer.gradle.credentials.domain.CredentialsContainer
import imaginate.BuildCredentials
import imaginate.stableDiffusionBuildApiKey

plugins {
    id("nu.studer.credentials")
}

val credentials: CredentialsContainer by extra

val buildCredentials = extensions.create("buildCredentials", BuildCredentials::class)
buildCredentials.stableDiffusionApiKey.convention(provider { credentials.stableDiffusionBuildApiKey })
