package net.codinux.accounting.ui.dialogs

import androidx.compose.runtime.Composable
import net.codinux.accounting.domain.common.model.error.ApplicationError
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ApplicationErrorDialog(error: ApplicationError, onDismiss: (() -> Unit)? = null) {
    val title = when (error.erroneousAction) {
        ErroneousAction.CreateInvoice -> Res.string.error_create_invoice
        ErroneousAction.AddEmailAccount -> Res.string.error_add_email_account
        ErroneousAction.FetchEmails -> Res.string.error_fetch_emails
        ErroneousAction.LoadFromDatabase -> Res.string.error_load_from_database
        ErroneousAction.SaveToDatabase -> Res.string.error_save_to_database
    }

    val errorMessage = if (error.errorMessageArguments.isNotEmpty()) stringResource(error.errorMessage, *error.errorMessageArguments.toTypedArray())
                    else stringResource(error.errorMessage)

    ErrorDialog(errorMessage, title, error.exception, onDismiss = onDismiss)
}