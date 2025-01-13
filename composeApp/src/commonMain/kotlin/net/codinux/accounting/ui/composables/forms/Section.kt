package net.codinux.accounting.ui.composables.forms

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    additionalElementAtEnd: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {

    RoundedCornersCard(Modifier.fillMaxWidth().padding(top = spaceBefore)) {
        if (additionalElementAtEnd == null) {
            Column(Modifier.fillMaxWidth().padding(all = Style.FormCardPadding).padding(vertical = innerVerticalPadding)) {
                SectionHeader(titleResource)

                if (nextElementIsTextField) {
                    Spacer(Modifier.height(6.dp))
                }

                content()
            }
        } else {
            Column(Modifier.fillMaxWidth().padding(all = Style.FormCardPadding).padding(bottom = innerVerticalPadding, top = innerVerticalPadding)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        SectionHeader(titleResource)
                    }

                    additionalElementAtEnd()
                }

                if (nextElementIsTextField) {
                    Spacer(Modifier.height(2.dp))
                }

                content()
            }
        }
    }
}