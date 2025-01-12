package net.codinux.accounting.ui.composables.forms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.codinux.accounting.ui.config.Colors
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SectionHeader(title: StringResource, fontSize: TextUnit = 16.sp, topPadding: Boolean = false) {
    SectionHeader(stringResource(title), fontSize, topPadding)
}

@Composable
fun SectionHeader(title: String, fontSize: TextUnit = 16.sp, topPadding: Boolean = false) {

    Text(
        text = title,
        modifier = Modifier.fillMaxWidth().let {
            if (topPadding) {
                it.padding(top = 24.dp)
            } else {
                it
            }
        },
        color = Colors.HighlightedTextColor,
        fontSize = fontSize,
        fontWeight = FontWeight.Medium
    )

}