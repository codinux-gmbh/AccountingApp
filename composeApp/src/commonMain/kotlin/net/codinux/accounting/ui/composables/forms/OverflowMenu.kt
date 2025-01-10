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
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.config.Style

@Composable
fun OverflowMenu(items: Collection<MenuItem>, buttonWidth: Dp = 48.dp, buttonHeight: Dp = Style.TextFieldsHeight, additionalMenuPadding: Dp = Style.MainScreenTabHorizontalPadding, iconColor: Color = Colors.FormValueTextColor) {
    var showMenu by remember { mutableStateOf(false) }

    val isCompactScreen = DI.uiState.isCompactScreen

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
            // don't get it why on compact screens (Android) we have to use 0.dp for perfect align whilst on larger screen we have to set a negative offset and it still doesn't perfectly align
            offset = DpOffset(x = if (isCompactScreen) 0.dp else -1 * (buttonWidth + additionalMenuPadding), y = 0.dp), // Offset to align to IconButton's end
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