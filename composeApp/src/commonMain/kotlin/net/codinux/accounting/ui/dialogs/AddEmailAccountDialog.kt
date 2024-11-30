package net.codinux.accounting.ui.dialogs

import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.codinux.accounting.domain.mail.model.MailAccountConfiguration
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.mail.AddEmailAccountDialogContent
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import org.jetbrains.compose.resources.stringResource


private val mailService = DI.mailService

@Composable
fun AddEmailAccountDialog() {

    val account by remember { mutableStateOf(MailAccountConfiguration()) }

    var isAddingAccount by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()


    fun dismiss() {
        DI.uiState.emails.showAddMailAccountDialog.value = false
    }

    fun addMailAccount() {
        coroutineScope.launch(Dispatchers.IO) {
            isAddingAccount = true

            val successful = mailService.addMailAccount(account)
            if (successful) {
                dismiss()
            }

            isAddingAccount = false
        }
    }


    BaseDialog(
        title = stringResource(Res.string.add_email_account),
        confirmButtonTitle = stringResource(Res.string.add),
        confirmButtonEnabled = isAddingAccount == false,
        showProgressIndicatorOnConfirmButton = isAddingAccount,
        backgroundColor = Colors.MainBackgroundColor,
        useMoreThanPlatformDefaultWidthOnMobile = true,
        callOnDismissAfterOnConfirm = false,
        onConfirm = { addMailAccount() },
        onDismiss = { dismiss() }
    ) {
        AddEmailAccountDialogContent(account)
    }
}