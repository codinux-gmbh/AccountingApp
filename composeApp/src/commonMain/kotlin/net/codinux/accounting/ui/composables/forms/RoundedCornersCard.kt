package net.codinux.accounting.ui.composables.forms

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoundedCornersCard(modifier: Modifier = Modifier, cornerSize: Dp = 12.dp, shadowElevation: Dp = 2.dp, content: @Composable () -> Unit) {
    Card(
        modifier,
        shape = RoundedCornerShape(cornerSize), // Rounded corners
        elevation = shadowElevation, // Shadow elevation
        content = content
    )
}