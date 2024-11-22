package net.codinux.accounting.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ToolbarButton(icon: ImageVector, label: String, onClick: () -> Unit) {

    IconButton(onClick, Modifier.width(120.dp).fillMaxHeight().padding(vertical = 4.dp, horizontal = 12.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Icon(icon, label, modifier = Modifier.size(32.dp))
            }

            Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }

}