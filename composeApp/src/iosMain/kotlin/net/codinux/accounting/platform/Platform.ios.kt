package net.codinux.accounting.platform

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIApplication
import platform.UIKit.UIKeyboardDidShowNotification
import platform.UIKit.UIKeyboardWillHideNotification

actual object Platform {

    actual val type: PlatformType = PlatformType.iOS

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