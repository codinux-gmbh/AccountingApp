package net.codinux.accounting.ui.composables.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.composables.ItemDivider

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> DropdownMenuBox(
    items: Collection<T>,
    onSelectedItemChanged: (T) -> Unit,
    getItemDisplayText: @Composable (T) -> String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    onExpandedChanged: ((Boolean) -> Unit)? = null,
    dropDownWidth: Dp? = null,
    addSeparatorAfterItem: Int? = null,
    dropDownItemContent: @Composable ((T) -> Unit)? = null,
    dropDownLabel: @Composable () -> Unit
) {

    var showDropDownMenu by remember { mutableStateOf(false) }

    fun expandedChanged(isExpanded: Boolean) {
        showDropDownMenu = isExpanded

        onExpandedChanged?.invoke(isExpanded)
    }

    ExposedDropdownMenuBox(showDropDownMenu, { expandedChanged(it) }, modifier) {
        dropDownLabel()

        // due to a bug (still not fixed since 2021) in ExposedDropdownMenu its popup has a maximum width of 800 pixel / 320dp which is too less to fit
        // TextField's width, see https://issuetracker.google.com/issues/205589613
        DropdownMenu(showDropDownMenu, { expandedChanged(false) }, Modifier.let { if (dropDownWidth != null) it.width(dropDownWidth) else it.exposedDropdownSize(true) }) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        expandedChanged(false)
                        onSelectedItemChanged(item)
                    }
                ) {
                    if (addSeparatorAfterItem == null) {
                        dropDownItemContent?.invoke(item) ?: Text(getItemDisplayText(item), maxLines = 1)
                    } else {
                        Column {
                            dropDownItemContent?.invoke(item) ?: Text(getItemDisplayText(item))

                            if (index == addSeparatorAfterItem - 1) { // index is 0-based, addSeparatorAfterItem 1-based
                                Spacer(Modifier.padding(top = 12.dp))
                                ItemDivider(thickness = 2.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}