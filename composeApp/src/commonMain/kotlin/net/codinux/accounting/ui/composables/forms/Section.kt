package net.codinux.accounting.ui.composables.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.config.Style
import org.jetbrains.compose.resources.StringResource


@Composable
fun Section(titleResource: StringResource, spaceBefore: Dp = 12.dp, innerVerticalPadding: Dp = 2.dp, content: @Composable () -> Unit) {
    RoundedCornersCard(Modifier.fillMaxWidth().padding(top = spaceBefore)) {
        Column(Modifier.fillMaxWidth().padding(all = Style.FormCardPadding).padding(vertical = innerVerticalPadding)) {
            SectionHeader(titleResource)

            content()
        }
    }
}