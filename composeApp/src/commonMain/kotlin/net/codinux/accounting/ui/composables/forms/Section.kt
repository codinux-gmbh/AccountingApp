package net.codinux.accounting.ui.composables.forms

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.config.Style
import org.jetbrains.compose.resources.StringResource

@Composable
fun Section(
    titleResource: StringResource,
    nextElementIsTextField: Boolean = false,
    spaceBefore: Dp = Style.SectionTopPadding,
    innerVerticalPadding: Dp = 0.dp,
    content: @Composable () -> Unit
) {

    RoundedCornersCard(Modifier.fillMaxWidth().padding(top = spaceBefore)) {
        Column(Modifier.fillMaxWidth().padding(all = Style.FormCardPadding).padding(vertical = innerVerticalPadding)) {
            SectionHeader(titleResource)

            if (nextElementIsTextField) {
                Spacer(Modifier.height(6.dp))
            }

            content()
        }
    }
}