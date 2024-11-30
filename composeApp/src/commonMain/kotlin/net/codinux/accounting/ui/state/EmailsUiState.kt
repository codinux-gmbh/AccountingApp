package net.codinux.accounting.ui.state

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import net.codinux.accounting.domain.mail.model.Email
import net.codinux.accounting.domain.mail.model.MailAccountConfiguration

class EmailsUiState : ViewModel() {

    val mails = MutableStateFlow<List<Email>>(emptyList())

    val mailAccounts = MutableStateFlow<List<MailAccountConfiguration>>(emptyList())


    val showOnlyEmailsWithInvoices = MutableStateFlow(false)


    val showAddMailAccountDialog = MutableStateFlow(false)

}