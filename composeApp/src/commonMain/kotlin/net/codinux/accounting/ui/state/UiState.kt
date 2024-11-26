package net.codinux.accounting.ui.state

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import net.codinux.accounting.domain.common.model.error.ApplicationError
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.mail.model.MailAccountConfiguration
import net.codinux.accounting.ui.tabs.MainScreenTab
import net.codinux.invoicing.mail.MailWithInvoice

class UiState : ViewModel() {

    val selectedMainScreenTab = MutableStateFlow(MainScreenTab.Postings)


    val mails = MutableStateFlow<List<MailWithInvoice>>(emptyList())

    val mailAccounts = MutableStateFlow<List<MailAccountConfiguration>>(emptyList())


    val showAddMailAccountDialog = MutableStateFlow(false)


    val applicationErrors = MutableStateFlow<List<ApplicationError>>(emptyList())

    fun errorOccurred(erroneousAction: ErroneousAction, errorMessage: String?, exception: Throwable? = null) {
        val message = errorMessage
            ?: exception?.message // TODO: find a better way to get error message from exception
            ?: exception?.let { it::class.simpleName }

        if (message != null) {
            errorOccurred(ApplicationError(erroneousAction, message, exception))
        }
    }

    fun errorOccurred(error: ApplicationError) {
        applicationErrors.value += error
    }

}