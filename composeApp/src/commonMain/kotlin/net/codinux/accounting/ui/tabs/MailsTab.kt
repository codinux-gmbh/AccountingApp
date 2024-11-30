package net.codinux.accounting.ui.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import net.codinux.accounting.ui.composables.mail.MailsList
import net.codinux.accounting.ui.dialogs.AddEmailAccountDialog
import net.codinux.accounting.ui.state.UiState

@Composable
fun MailsTab(uiState: UiState) {

    val mails = uiState.emails.mails.collectAsState().value

    val showAddMailAccountDialog by uiState.emails.showAddMailAccountDialog.collectAsState()


    MailsList(mails)


    if (showAddMailAccountDialog) {
        AddEmailAccountDialog()
    }

}