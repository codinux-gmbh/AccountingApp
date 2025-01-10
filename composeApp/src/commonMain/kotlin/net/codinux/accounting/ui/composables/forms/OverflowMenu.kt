package net.codinux.accounting.ui.composables.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import net.codinux.accounting.ui.composables.forms.model.MenuItem
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.Style

@Composable
fun OverflowMenu(items: Collection<MenuItem>, buttonWidth: Dp = 48.dp, buttonHeight: Dp = Style.TextFieldsHeight, additionalMenuPadding: Dp = Style.MainScreenTabHorizontalPadding, iconColor: Color = Colors.FormValueTextColor) {
    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.width(buttonWidth)) {
        IconButton(
            onClick = { showMenu = true },
            modifier = Modifier.width(buttonWidth).height(buttonHeight).align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Show menu with more options",
                tint = iconColor
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            offset = DpOffset(x = -1 * (buttonWidth + additionalMenuPadding), y = 0.dp), // Offset to align to IconButton's end
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                        item.onClick()
                    },
                    content = { item.content() }
                )
            }
        }
    }
}