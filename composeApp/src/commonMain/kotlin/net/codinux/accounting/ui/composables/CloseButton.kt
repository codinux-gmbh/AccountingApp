package net.codinux.accounting.ui.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.config.Style

@Composable
fun CloseButton(contentDescription: String = "Close dialog", color: Color = Style.DialogTitleTextColor, size: Dp = 32.dp, onClick: () -> Unit) {
    TextButton(onClick, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent), contentPadding = PaddingValues(0.dp), modifier = Modifier.size(size)) {
        Icon(Icons.Filled.Close, contentDescription = contentDescription, Modifier.size(size), tint = color)
    }
}