package net.codinux.accounting.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.config.Style
import net.codinux.accounting.ui.tabs.MainScreenTab

@Composable
fun ToolbarButton(tab: MainScreenTab, selectedTab: MainScreenTab, icon: ImageVector, label: String) {

    val color = LocalContentColor.current.copy(alpha = if (tab == selectedTab) ContentAlpha.medium else ContentAlpha.disabled)

    IconButton({ DI.uiState.selectedMainScreenTab.value = tab }, Modifier.width(100.dp).fillMaxHeight().padding(vertical = 4.dp, horizontal = 12.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Icon(icon, label, Modifier.size(24.dp), color)
            }

            Text(label, color = color, fontSize = Style.ToolbarButtonFontSize, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }

}