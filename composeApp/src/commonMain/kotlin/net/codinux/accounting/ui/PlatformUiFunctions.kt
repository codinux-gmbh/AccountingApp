package net.codinux.accounting.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import net.codinux.accounting.ui.state.ScreenSizeInfo

expect object PlatformUiFunctions {

    fun createImageBitmap(imageBytes: ByteArray): ImageBitmap

    @Composable
    fun rememberScreenSize(): ScreenSizeInfo

}