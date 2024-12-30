package net.codinux.accounting.ui.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import net.codinux.accounting.domain.mail.service.MailService
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.TextOfMaxWidth
import net.codinux.accounting.ui.composables.mail.MailsList
import net.codinux.accounting.ui.dialogs.AddEmailAccountDialog
import net.codinux.accounting.ui.state.EmailsUiState

@Composable
fun MailsTab(mailService: MailService, uiState: EmailsUiState) {

    val mails = uiState.mails.collectAsState().value

    val showAddMailAccountDialog by uiState.showAddMailAccountDialog.collectAsState()


    if (mails.isEmpty()) {
        TextOfMaxWidth(Res.string.reason_for_adding_email_account)
    } else {
        MailsList(uiState, mails)
    }


    if (showAddMailAccountDialog) {
        AddEmailAccountDialog(mailService)
    }

}