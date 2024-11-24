package net.codinux.accounting.ui.composables.forms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.codinux.accounting.ui.config.Colors

@Composable
fun SectionHeader(title: String, topPadding: Boolean = true) {

    Text(
        text = title,
        modifier = Modifier.fillMaxWidth().let {
            if (topPadding) {
                it.padding(top = 24.dp)
            } else {
                it
            }
        },
        color = Colors.CodinuxSecondaryColor,
        fontSize = 16.sp
    )

}