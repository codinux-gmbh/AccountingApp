package net.codinux.accounting.ui.state

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ScreenSizeInfo(
    val heightDp: Dp,
    val widthDp: Dp
) {
    val uiType = when (widthDp) {
        in 0.dp..600.dp -> UiType.Compact
        in 600.dp..840.dp -> UiType.Medium
        else -> UiType.Expanded
    }

    override fun toString() = "$widthDp x $heightDp"
}

enum class UiType {
    Compact,
    Medium,
    Expanded;

    val isCompactScreen by lazy { this == Compact }
}
