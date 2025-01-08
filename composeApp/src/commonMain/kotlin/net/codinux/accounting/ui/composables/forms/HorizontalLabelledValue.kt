package net.codinux.accounting.ui.composables.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.config.Style
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HorizontalLabelledValue(label: StringResource, value: String?, labelWidth: Dp? = null, valueTextColor: Color? = null, topPadding: Dp = Style.SectionTopPadding, bottomPadding: Dp = 4.dp, labelMaxLines: Int = 1) {

    val isCompactScreen = DI.uiState.uiType.collectAsState().value.isCompactScreen

    val width = labelWidth ?: if (isCompactScreen) 150.dp else 220.dp


    Row(Modifier.fillMaxWidth().padding(top = topPadding, bottom = bottomPadding), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(label),
            modifier = Modifier.width(width),
            fontSize = Style.LabelledValueFontSize,
            color = Colors.FormLabelTextColor,
            maxLines = labelMaxLines,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = value ?: "",
            modifier = Modifier,
            fontSize = Style.LabelledValueFontSize,
            color = valueTextColor ?: Colors.FormValueTextColor
        )
    }

}