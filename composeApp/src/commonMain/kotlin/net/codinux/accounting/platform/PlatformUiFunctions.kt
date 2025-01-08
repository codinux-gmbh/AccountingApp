package net.codinux.accounting.platform

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import net.codinux.accounting.ui.state.ScreenSizeInfo

expect object PlatformUiFunctions {

    fun createImageBitmap(imageBytes: ByteArray): ImageBitmap

    @Composable
    fun rememberScreenSize(): ScreenSizeInfo

    @Composable
    fun systemPaddings(): PaddingValues

    fun addKeyboardVisibilityListener(onKeyboardVisibilityChanged: (Boolean) -> Unit)

}