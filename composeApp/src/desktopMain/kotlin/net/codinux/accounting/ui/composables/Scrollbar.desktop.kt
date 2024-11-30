package net.codinux.accounting.ui.composables

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.text.TextFieldScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun VerticalScrollbar(
    state: ScrollableState,
    modifier: Modifier,
    reverseLayout: Boolean,
    interactionSource: MutableInteractionSource
) {

    if (state is ScrollState) {
        VerticalScrollbar(rememberScrollbarAdapter(state), modifier, reverseLayout, interactionSource = interactionSource)
    } else if (state is LazyListState) {
        VerticalScrollbar(rememberScrollbarAdapter(state), modifier, reverseLayout, interactionSource = interactionSource)
    } else if (state is LazyGridState) {
        VerticalScrollbar(rememberScrollbarAdapter(state), modifier, reverseLayout, interactionSource = interactionSource)
    } else if (state is TextFieldScrollState) {
        VerticalScrollbar(rememberScrollbarAdapter(state), modifier, reverseLayout, interactionSource = interactionSource)
    }
}