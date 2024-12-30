package net.codinux.accounting.ui.composables

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun VerticalScrollbar(
    state: ScrollableState,
    modifier: Modifier,
    reverseLayout: Boolean,
    interactionSource: MutableInteractionSource
) {
    // no-op on WasmJs / not implemented on WasmJs
}