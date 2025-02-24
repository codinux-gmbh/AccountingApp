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
    modifier: Modifier = Modifier.fillMaxWidth(),
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

    DropdownMenuBox(items, onSelectedItemChanged, getItemDisplayText, modifier, { showDropDownMenu = it }, dropDownWidth, addSeparatorAfterItem, dropDownItemContent) {
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
    }

}