package net.codinux.accounting.ui.composables.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.composables.ItemDivider
import org.jetbrains.compose.resources.StringResource

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> Select(
    label: StringResource?,
    items: Collection<T>,
    selectedItem: T,
    onSelectedItemChanged: (T) -> Unit,
    getItemDisplayText: @Composable (T) -> String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    textColor: Color? = null,
    textStyle: TextStyle? = null,
    backgroundColor: Color = Color.Transparent,
    leadingIcon: @Composable (() -> Unit)? = null,
    dropDownWidth: Dp? = null,
    addSeparatorAfterItem: Int? = null,
    dropDownItemContent: @Composable ((T) -> Unit)? = null
) {
    var showDropDownMenu by remember { mutableStateOf(false) }

    val effectiveTextStyle = (textStyle ?: LocalTextStyle.current).let {
        if (textColor != null) it.copy(textColor) else it
    }

    ExposedDropdownMenuBox(showDropDownMenu, { isExpanded -> showDropDownMenu = isExpanded }, modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = getItemDisplayText(selectedItem),
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            textStyle = effectiveTextStyle,
            label = label,
            readOnly = true,
            required = required,
            maxLines = 1,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showDropDownMenu) },
            leadingIcon = leadingIcon,
            backgroundColor = backgroundColor
        )

        // due to a bug (still not fixed since 2021) in ExposedDropdownMenu its popup has a maximum width of 800 pixel / 320dp which is too less to fit
        // TextField's width, see https://issuetracker.google.com/issues/205589613
        DropdownMenu(showDropDownMenu, { showDropDownMenu = false }, Modifier.let { if (dropDownWidth != null) it.width(dropDownWidth) else it.exposedDropdownSize(true) }) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        showDropDownMenu = false
                        onSelectedItemChanged(item)
                    }
                ) {
                    if (addSeparatorAfterItem == null) {
                        dropDownItemContent?.invoke(item) ?: Text(getItemDisplayText(item))
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