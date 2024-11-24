package net.codinux.accounting.ui.dialogs

import androidx.compose.runtime.Composable
import net.codinux.accounting.ui.composables.forms.datetime.DatePickerDialogView
import java.time.LocalDate

@Composable
fun DatePickerDialog(
    selectedDate: LocalDate? = null,
    dateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {

    BaseDialog(
        titleBarVisible = false,
        confirmButtonVisible = false,
        onDismiss = onDismiss
    ) {
        DatePickerDialogView(selectedDate) { selectedDate ->
            dateSelected(selectedDate)
            onDismiss()
        }
    }
}