package net.codinux.accounting.ui.composables.forms.datetime

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import net.codinux.accounting.domain.common.extensions.toEInvoicingDate
import net.codinux.accounting.domain.common.extensions.toKotlinLocalDate
import net.codinux.accounting.ui.composables.forms.OutlinedTextField
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.dialogs.DatePickerDialog
import org.jetbrains.compose.resources.StringResource


private val formatUtil = DI.formatUtil

@Composable
fun DatePicker(
    label: StringResource?,
    selectedDate: net.codinux.invoicing.model.LocalDate? = null,
    modifier: Modifier = Modifier.width(if (DI.uiState.isCompactScreen) 86.dp else 90.dp).heightIn(min = 45.dp),
    showCalendarIcon: Boolean = false,
    moveFocusOnToNextElementOnSelection: Boolean = true,
    textColor: Color? = null,
    required: Boolean = false,
    dateSelected: (net.codinux.invoicing.model.LocalDate) -> Unit
) {
    DatePicker(label, selectedDate?.toKotlinLocalDate(), modifier, showCalendarIcon, moveFocusOnToNextElementOnSelection, textColor, required) { dateSelected(it.toEInvoicingDate()) }
}

@Composable
fun DatePicker(
    label: StringResource?,
    selectedDate: LocalDate? = null,
    modifier: Modifier = Modifier.width(if (DI.uiState.isCompactScreen) 86.dp else 90.dp).heightIn(min = 45.dp),
    showCalendarIcon: Boolean = false,
    moveFocusOnToNextElementOnSelection: Boolean = true,
    textColor: Color? = null,
    required: Boolean = false,
    dateSelected: (LocalDate) -> Unit
) {

    var showDatePickerDialog by remember { mutableStateOf(false) }

    var hasDateBeenSelected by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current


    Column(modifier.clickableWithRipple { showDatePickerDialog = true }, verticalArrangement = Arrangement.Center) {
        OutlinedTextField(
            value = selectedDate?.let { formatUtil.formatShortDate(it) } ?: "",
            onValueChange = { },
            modifier = Modifier.fillMaxSize().onFocusEvent { state -> if (state.isFocused || state.hasFocus) { showDatePickerDialog = true } },
            textStyle = if (textColor != null) TextStyle(textColor) else LocalTextStyle.current,
            label = label,
            readOnly = true,
            required = required,
            maxLines = 1,
            trailingIcon = if (showCalendarIcon) { { Icon(Icons.Outlined.CalendarMonth, "Select date") } } else null,
        )
    }

    if (showDatePickerDialog) {
        DatePickerDialog(selectedDate, dateSelected = { selectedDate ->
            dateSelected(selectedDate)
            hasDateBeenSelected = true
        }) {
            showDatePickerDialog = false

            // remove focus from picker's OutlinedTextField so that it's focusable again and therefore onFocusEvent { } fires again
            if (hasDateBeenSelected && moveFocusOnToNextElementOnSelection) {
                focusManager.moveFocus(FocusDirection.Next)
            } else {
                focusManager.clearFocus(true)
            }
        }
    }

}