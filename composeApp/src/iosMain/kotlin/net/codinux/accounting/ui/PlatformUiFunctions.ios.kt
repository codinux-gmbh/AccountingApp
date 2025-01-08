package net.codinux.accounting.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import net.codinux.accounting.ui.state.ScreenSizeInfo
import org.jetbrains.skia.Image
import platform.CoreGraphics.CGRect
import platform.UIKit.UIScreen

actual object PlatformUiFunctions {

    actual fun createImageBitmap(imageBytes: ByteArray): ImageBitmap =
        Image.makeFromEncoded(imageBytes).toComposeImageBitmap()

    @OptIn(ExperimentalForeignApi::class)
    @Composable
    actual fun rememberScreenSizeInfo(): ScreenSizeInfo {
        val density = LocalDensity.current
        val screenBounds: CGRect = UIScreen.mainScreen.bounds.useContents { this }
        val screenWidth = screenBounds.size.width
        val screenHeight = screenBounds.size.height

        return remember(density, screenWidth, screenHeight) {
            ScreenSizeInfo(
                heightDp = with(density) { screenHeight.dp },
                widthDp = with(density) { screenWidth.dp }
            )
        }
    }

}