package net.codinux.accounting.ui.dialogs

import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.codinux.accounting.domain.mail.model.MailAccountConfiguration
import net.codinux.accounting.domain.mail.service.MailService
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.mail.AddEmailAccountDialogContent
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.invoicing.email.model.EmailAccount
import org.jetbrains.compose.resources.stringResource


@Composable
fun AddEmailAccountDialog(mailService: MailService) {

    val account by remember { mutableStateOf(MailAccountConfiguration()) }

    fun isAccountValid(account: EmailAccount?) =
        account == null || (account.username.isNotBlank() && account.password.isNotBlank() && account.serverAddress.isNotBlank() && (account.port == null || (account.port ?: 0) > 0))

    val isValid by remember(account.receiveEmailConfiguration, account.receiveEmailConfiguration?.username, account.receiveEmailConfiguration?.password, account.receiveEmailConfiguration?.serverAddress, account.receiveEmailConfiguration?.port,
        account.sendEmailConfiguration, account.sendEmailConfiguration?.username, account.sendEmailConfiguration?.password, account.sendEmailConfiguration?.serverAddress, account.sendEmailConfiguration?.port) {
        derivedStateOf {
            (account.receiveEmailConfiguration != null || account.sendEmailConfiguration != null) &&
                    isAccountValid(account.receiveEmailConfiguration) && isAccountValid(account.sendEmailConfiguration)
        }
    }

    var isAddingAccount by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()


    fun dismiss() {
        DI.uiState.emails.showAddMailAccountDialog.value = false
    }

    fun addMailAccount() {
        coroutineScope.launch(Dispatchers.IO) {
            isAddingAccount = true

            val successful = mailService.addMailAccount(account, coroutineScope)
            if (successful) {
                dismiss()
            }

            isAddingAccount = false
        }
    }


    BaseDialog(
        title = stringResource(Res.string.add_email_account),
        confirmButtonTitle = stringResource(Res.string.add),
//        confirmButtonEnabled = isValid, // does not work as MailAccountConfiguration is not a ViewModel so we're not getting notified about changes
        showProgressIndicatorOnConfirmButton = isAddingAccount,
        backgroundColor = Colors.MainBackgroundColor,
        useMoreThanPlatformDefaultWidthOnSmallScreens = true,
        callOnDismissAfterOnConfirm = false,
        onConfirm = { addMailAccount() },
        onDismiss = { dismiss() }
    ) {
        AddEmailAccountDialogContent(account)
    }
}