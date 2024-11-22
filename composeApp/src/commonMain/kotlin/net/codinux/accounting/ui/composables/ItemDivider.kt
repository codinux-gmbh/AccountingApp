package net.codinux.accounting.ui.composables

import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.Style

@Composable
fun ItemDivider(color: Color = Colors.ItemDividerColor, thickness: Dp = Style.DividerThickness, modifier: Modifier = Modifier) {
    Divider(color = color, thickness = thickness, modifier = modifier)
}