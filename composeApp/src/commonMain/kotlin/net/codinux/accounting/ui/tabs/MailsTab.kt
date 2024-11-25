package net.codinux.accounting.ui.tabs

import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.codinux.accounting.ui.composables.mail.MailsList
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.dialogs.AddEmailAccountDialog
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.mail.MailWithInvoice


private val mailService = DI.mailService

@Composable
fun MailsTab(uiState: UiState) {

    var isInitialized by remember { mutableStateOf(false) }

    var mails by remember { mutableStateOf(listOf<MailWithInvoice>()) }

    val showAddMailAccountDialog by uiState.showAddMailAccountDialog.collectAsState()

    val coroutineScope = rememberCoroutineScope()


    MailsList(mails)


    if (showAddMailAccountDialog) {
        AddEmailAccountDialog()
    }


    LaunchedEffect(isInitialized) {
        coroutineScope.launch(Dispatchers.IO) {
            mails = mailService.loadPersistedMails()

            isInitialized = true
        }
    }

}