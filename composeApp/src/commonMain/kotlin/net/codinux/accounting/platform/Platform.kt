package net.codinux.accounting.platform

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

expect object Platform {

    val type: PlatformType


    @Composable
    fun systemPaddings(): PaddingValues

    fun addKeyboardVisibilityListener(onKeyboardVisibilityChanged: (Boolean) -> Unit)

}


val Platform.isAndroid: Boolean
    get() = this.type == PlatformType.Android

val Platform.isIOS: Boolean
    get() = this.type == PlatformType.iOS