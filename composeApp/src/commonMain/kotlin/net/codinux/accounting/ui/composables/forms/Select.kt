package net.codinux.accounting.ui.composables.forms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
    leadingIcon: @Composable (() -> Unit)? = null,
    dropDownItemContent: @Composable ((T) -> Unit)? = null
) {
    var showDropDownMenu by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(showDropDownMenu, { isExpanded -> showDropDownMenu = isExpanded }, modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = getItemDisplayText(selectedItem),
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            textStyle = if (textColor != null) TextStyle(textColor) else LocalTextStyle.current,
            label = label,
            readOnly = true,
            required = required,
            maxLines = 1,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showDropDownMenu) },
            leadingIcon = leadingIcon
        )

        // due to a bug (still not fixed since 2021) in ExposedDropdownMenu its popup has a maximum width of 800 pixel / 320dp which is too less to fit
        // TextField's width, see https://issuetracker.google.com/issues/205589613
        DropdownMenu(showDropDownMenu, { showDropDownMenu = false }, Modifier.exposedDropdownSize(true)) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        showDropDownMenu = false
                        onSelectedItemChanged(item)
                    }
                ) {
                    dropDownItemContent?.invoke(item) ?: Text(getItemDisplayText(item))
                }
            }
        }
    }

}