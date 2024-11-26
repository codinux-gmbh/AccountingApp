package net.codinux.accounting.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import net.codinux.accounting.ui.dialogs.ApplicationErrorDialog
import net.codinux.accounting.ui.state.UiState

@Composable
fun StateHandler(uiState: UiState) {

    val applicationErrors = uiState.applicationErrors

    val currentError = applicationErrors.collectAsState().value.firstOrNull()


    if (currentError != null) {
        ApplicationErrorDialog(currentError) {
            applicationErrors.value -= currentError
        }
    }

}