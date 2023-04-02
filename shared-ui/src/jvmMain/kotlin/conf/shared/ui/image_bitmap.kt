package conf.shared.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

actual fun imageBitmapFromBytes(encodedImageData: ByteArray): ImageBitmap =
    Image.makeFromEncoded(encodedImageData).toComposeImageBitmap()
