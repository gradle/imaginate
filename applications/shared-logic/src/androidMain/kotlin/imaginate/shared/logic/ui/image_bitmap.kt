package imaginate.shared.logic.ui

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun imageBitmapFromBytes(encodedImageData: ByteArray): ImageBitmap =
    BitmapFactory.decodeByteArray(encodedImageData, 0, encodedImageData.size).asImageBitmap()
