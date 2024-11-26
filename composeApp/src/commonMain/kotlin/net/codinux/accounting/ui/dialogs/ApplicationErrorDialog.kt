package net.codinux.accounting.ui.dialogs

import androidx.compose.runtime.Composable
import net.codinux.accounting.domain.common.model.error.ApplicationError
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.resources.*

@Composable
fun ApplicationErrorDialog(error: ApplicationError, onDismiss: (() -> Unit)? = null) {
    val title = when (error.erroneousAction) {
        ErroneousAction.AddEmailAccount -> Res.string.error_add_email_account
        ErroneousAction.SaveToDatabase -> Res.string.error_save_to_database
    }

    ErrorDialog(error.errorMessage, title, error.exception, onDismiss = onDismiss)
}