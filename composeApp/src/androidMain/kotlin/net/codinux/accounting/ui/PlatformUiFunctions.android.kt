package net.codinux.accounting.ui

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual object PlatformUiFunctions {

    actual fun createImageBitmap(imageBytes: ByteArray): ImageBitmap =
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size).asImageBitmap()

}