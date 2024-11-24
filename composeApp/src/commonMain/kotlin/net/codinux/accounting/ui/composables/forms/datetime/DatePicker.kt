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
import androidx.compose.ui.unit.sp
import net.codinux.accounting.platform.Platform
import net.codinux.accounting.platform.isDesktop
import net.codinux.accounting.ui.composables.forms.OutlinedTextField
import net.codinux.accounting.ui.dialogs.DatePickerDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun DatePicker(
    label: String,
    selectedDate: LocalDate? = null,
    modifier: Modifier = Modifier.width(if (Platform.isDesktop) 90.dp else 86.dp).heightIn(min = 45.dp),
    showCalendarIcon: Boolean = false,
    moveFocusOnToNextElementOnSelection: Boolean = true,
    dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT),
    textColor: Color? = null,
    dateSelected: (LocalDate) -> Unit
) {

    var showDatePickerDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current


    Column(modifier.clickable { showDatePickerDialog = true }, verticalArrangement = Arrangement.Center) {
        OutlinedTextField(
            value = selectedDate?.let { dateFormatter.format(it) } ?: "",
            onValueChange = { },
            modifier = Modifier.fillMaxSize().onFocusEvent { state -> if (state.isFocused || state.hasFocus) { showDatePickerDialog = true } },
            textStyle = if (textColor != null) TextStyle(textColor) else LocalTextStyle.current,
            label = { Text(label, fontSize = 10.sp, color = textColor ?: Color.Unspecified) },
            readOnly = true,
            maxLines = 1,
            trailingIcon = if (showCalendarIcon) { { Icon(Icons.Outlined.CalendarMonth, "Select date") } } else null,
        )
    }

    if (showDatePickerDialog) {
        DatePickerDialog(selectedDate, dateSelected) {
            showDatePickerDialog = false

            // remove focus from picker's OutlinedTextField so that it's focusable again and therefore onFocusEvent { } fires again
            if (moveFocusOnToNextElementOnSelection) {
                focusManager.moveFocus(FocusDirection.Next)
            } else {
                focusManager.clearFocus(true)
            }
        }
    }

}