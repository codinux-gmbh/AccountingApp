package net.codinux.accounting.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.state.ScreenSizeInfo
import org.jetbrains.skia.Image

actual object PlatformUiFunctions {

    actual fun createImageBitmap(imageBytes: ByteArray): ImageBitmap =
        Image.makeFromEncoded(imageBytes).toComposeImageBitmap()

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    actual fun rememberScreenSize(): ScreenSizeInfo {
        val density = LocalDensity.current
        val config = LocalWindowInfo.current.containerSize

        return remember(density, config) {
            ScreenSizeInfo(
                heightDp = with(density) { config.height.toDp() },
                widthDp = with(density) { config.width.toDp() }
            )
        }
    }

    @Composable
    actual fun systemPaddings(): PaddingValues = PaddingValues(0.dp)

    actual fun addKeyboardVisibilityListener(onKeyboardVisibilityChanged: (Boolean) -> Unit) {
        // no-op
    }

}