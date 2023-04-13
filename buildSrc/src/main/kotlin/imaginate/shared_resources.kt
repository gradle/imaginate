package imaginate

import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.attributes.Attribute

internal
interface ImageFormat : Named {
    companion object {

        val IMAGE_FORMAT_ATTRIBUTE: Attribute<ImageFormat> =
            Attribute.of("imageFormat", ImageFormat::class.java)

        val BITMAP = "bitmap"
        val DRAWABLE = "drawable"
    }
}

internal
fun ConfigurationContainer.registerOutgoingImages(
    name: String,
    format: ImageFormat,
): NamedDomainObjectProvider<Configuration> =
    register(name) {
        isCanBeResolved = false
        isCanBeConsumed = true
        attributes {
            attribute(ImageFormat.IMAGE_FORMAT_ATTRIBUTE, format)
        }
    }


internal
fun ConfigurationContainer.registerIncomingImages(
    name: String,
    format: ImageFormat,
): NamedDomainObjectProvider<Configuration> =
    register(name) {
        isCanBeResolved = true
        isCanBeConsumed = false
        attributes {
            attribute(ImageFormat.IMAGE_FORMAT_ATTRIBUTE, format)
        }
    }
