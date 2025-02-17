package net.codinux.accounting.ui.composables

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import net.codinux.accounting.ui.config.Style
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HeaderText(title: StringResource, modifier: Modifier = Modifier, textAlign: TextAlign = TextAlign.Start, textColor: Color = Style.HeaderTextColor, fontSize: TextUnit = Style.HeaderFontSize) {
    HeaderText(stringResource(title), modifier, textAlign, textColor, fontSize)
}

@Composable
fun HeaderText(title: String, modifier: Modifier = Modifier, textAlign: TextAlign = TextAlign.Start, textColor: Color = Style.HeaderTextColor, fontSize: TextUnit = Style.HeaderFontSize) {
    Text(
        title,
        color = textColor,
        fontSize = fontSize,
        fontWeight = Style.HeaderFontWeight,
        modifier = modifier,
        textAlign = textAlign
    )
}