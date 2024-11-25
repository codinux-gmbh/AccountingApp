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

    val coroutineScope = rememberCoroutineScope()

    fun addMailAccount() {
        coroutineScope.launch(Dispatchers.IO) {
            mailService.addMailAccount(account)
        }

    }

    BaseDialog(
        title = stringResource(Res.string.add_email_account),
        confirmButtonTitle = stringResource(Res.string.add),
        backgroundColor = Colors.MainBackgroundColor,
        useMoreThanPlatformDefaultWidthOnMobile = true,
        onConfirm = { addMailAccount() },
        onDismiss = { DI.uiState.showAddMailAccountDialog.value = false }
    ) {
        AddEmailAccountDialogContent(account)
    }
}