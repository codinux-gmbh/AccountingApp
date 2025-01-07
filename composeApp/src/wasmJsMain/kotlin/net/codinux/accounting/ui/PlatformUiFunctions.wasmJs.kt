package net.codinux.accounting.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

actual object PlatformUiFunctions {

    actual fun createImageBitmap(imageBytes: ByteArray): ImageBitmap =
        Image.makeFromEncoded(imageBytes).toComposeImageBitmap()

}