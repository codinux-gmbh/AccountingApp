package net.codinux.accounting.ui

import androidx.compose.ui.graphics.ImageBitmap

expect object PlatformUiFunctions {

    fun createImageBitmap(imageBytes: ByteArray): ImageBitmap

}