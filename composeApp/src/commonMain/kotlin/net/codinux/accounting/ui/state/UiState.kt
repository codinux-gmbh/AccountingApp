package net.codinux.accounting.ui.state

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import net.codinux.accounting.ui.tabs.MainScreenTab
import net.codinux.invoicing.mail.MailWithInvoice

class UiState : ViewModel() {

    val selectedMainScreenTab = MutableStateFlow(MainScreenTab.Postings)


    val mails = MutableStateFlow<List<MailWithInvoice>>(emptyList())


    val showAddMailAccountDialog = MutableStateFlow(false)

}