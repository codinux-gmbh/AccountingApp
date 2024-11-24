package net.codinux.accounting.ui.composables

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import net.codinux.accounting.ui.config.Style

@Composable
fun HeaderText(title: String, modifier: Modifier = Modifier, textAlign: TextAlign = TextAlign.Start, textColor: Color = Style.HeaderTextColor) {
    Text(
        title,
        color = textColor,
        fontSize = Style.HeaderFontSize,
        fontWeight = Style.HeaderFontWeight,
        modifier = modifier,
        textAlign = textAlign
    )
}