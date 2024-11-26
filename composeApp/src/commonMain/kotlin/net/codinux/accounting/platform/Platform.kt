package net.codinux.accounting.platform

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

expect object Platform {

    val type: PlatformType


    val supportsCreatingPdfs: Boolean

    val supportsValidatingXml: Boolean


    @Composable
    fun systemPaddings(): PaddingValues

    fun addKeyboardVisibilityListener(onKeyboardVisibilityChanged: (Boolean) -> Unit)

}


val Platform.isMobile: Boolean
    get() = type == PlatformType.iOS || type == PlatformType.Android // TODO: for Web check if it's a mobile browser, but very low priority

val Platform.isDesktop: Boolean
    get() = !isMobile