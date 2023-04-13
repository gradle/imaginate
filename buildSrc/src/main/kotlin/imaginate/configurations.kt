package imaginate

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer

internal
fun ConfigurationContainer.registerDependencyBucket(name: String): Configuration =
    register(name) {
        isCanBeResolved = true
        isCanBeConsumed = false
    }.get()
