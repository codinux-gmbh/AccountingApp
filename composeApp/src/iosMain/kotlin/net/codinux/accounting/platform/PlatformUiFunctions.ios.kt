package net.codinux.accounting.platform

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
import platform.UIKit.UIScreen
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectGetHeight
import platform.CoreGraphics.CGRectGetWidth
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIApplication
import platform.UIKit.UIKeyboardDidShowNotification
import platform.UIKit.UIKeyboardWillHideNotification

actual object PlatformUiFunctions {

    actual fun createImageBitmap(imageBytes: ByteArray): ImageBitmap =
        Image.makeFromEncoded(imageBytes).toComposeImageBitmap()

    @OptIn(ExperimentalForeignApi::class)
    @Composable
    actual fun rememberScreenSize(): ScreenSizeInfo {
        val density = LocalDensity.current
        val screenBounds = UIScreen.mainScreen.bounds
        val screenWidth = CGRectGetWidth(screenBounds)
        val screenHeight = CGRectGetHeight(screenBounds)

        return remember(density, screenWidth, screenHeight) {
            ScreenSizeInfo(
                heightDp = with(density) { screenHeight.dp },
                widthDp = with(density) { screenWidth.dp }
            )
        }
    }


    @Composable
    @OptIn(ExperimentalForeignApi::class)
    actual fun systemPaddings(): PaddingValues {
        val window = UIApplication.sharedApplication.keyWindow ?: return PaddingValues(0.dp)
        val (top, bottom) = window.safeAreaInsets.useContents {
            this.top.toFloat() to this.bottom.toFloat()
        }

        return PaddingValues(top = top.dp, bottom = bottom.dp)
    }

    actual fun addKeyboardVisibilityListener(onKeyboardVisibilityChanged: (Boolean) -> Unit) {
        val notificationCenter = NSNotificationCenter.defaultCenter

        notificationCenter.addObserverForName(
            name = UIKeyboardDidShowNotification,
            `object` = null,
            queue = null
        ) { _ -> onKeyboardVisibilityChanged(true) }

        notificationCenter.addObserverForName(
            name = UIKeyboardWillHideNotification,
            `object` = null,
            queue = null
        ) { _ -> onKeyboardVisibilityChanged(false) }
    }

}