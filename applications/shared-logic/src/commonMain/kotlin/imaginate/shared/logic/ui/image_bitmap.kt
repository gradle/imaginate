package imaginate.shared.logic.ui

import androidx.compose.ui.graphics.ImageBitmap

expect fun imageBitmapFromBytes(encodedImageData: ByteArray): ImageBitmap
