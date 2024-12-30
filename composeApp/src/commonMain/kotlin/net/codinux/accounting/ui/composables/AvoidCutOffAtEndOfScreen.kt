package net.codinux.accounting.ui.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.codinux.accounting.ui.config.Style

@Composable
fun AvoidCutOffAtEndOfScreen() {
    Spacer(Modifier.padding(bottom = Style.ScreenVerticalPadding))
}