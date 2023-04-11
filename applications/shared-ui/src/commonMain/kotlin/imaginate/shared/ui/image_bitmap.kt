package imaginate.shared.ui

import androidx.compose.ui.graphics.ImageBitmap

expect fun imageBitmapFromBytes(encodedImageData: ByteArray): ImageBitmap
