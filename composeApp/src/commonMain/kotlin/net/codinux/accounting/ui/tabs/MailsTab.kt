package net.codinux.accounting.ui.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import net.codinux.accounting.ui.composables.mail.MailsList
import net.codinux.accounting.ui.dialogs.AddEmailAccountDialog
import net.codinux.accounting.ui.state.EmailsUiState

@Composable
fun MailsTab(uiState: EmailsUiState) {

    val mails = uiState.mails.collectAsState().value

    val showAddMailAccountDialog by uiState.showAddMailAccountDialog.collectAsState()


    MailsList(uiState, mails)


    if (showAddMailAccountDialog) {
        AddEmailAccountDialog()
    }

}