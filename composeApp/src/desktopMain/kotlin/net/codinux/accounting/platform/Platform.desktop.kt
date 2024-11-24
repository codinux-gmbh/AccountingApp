package net.codinux.accounting.platform

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

actual object Platform {

    actual val type = PlatformType.JVM


    @Composable
    actual fun systemPaddings(): PaddingValues = PaddingValues(0.dp)

    actual fun addKeyboardVisibilityListener(onKeyboardVisibilityChanged: (Boolean) -> Unit) {
        // no-op
    }

}