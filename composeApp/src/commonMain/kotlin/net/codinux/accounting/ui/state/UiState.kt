package net.codinux.accounting.ui.state

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import net.codinux.accounting.domain.common.model.error.ApplicationError
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.mail.model.MailAccountConfiguration
import net.codinux.accounting.ui.tabs.MainScreenTab
import net.codinux.invoicing.mail.MailWithInvoice
import org.jetbrains.compose.resources.StringResource

class UiState : ViewModel() {

    val selectedMainScreenTab = MutableStateFlow(MainScreenTab.Mails)


    val mails = MutableStateFlow<List<MailWithInvoice>>(emptyList())

    val mailAccounts = MutableStateFlow<List<MailAccountConfiguration>>(emptyList())


    val showAddMailAccountDialog = MutableStateFlow(false)


    val applicationErrors = MutableStateFlow<List<ApplicationError>>(emptyList())

    fun errorOccurred(erroneousAction: ErroneousAction, errorMessage: StringResource, exception: Throwable? = null) {
        errorOccurred(ApplicationError(erroneousAction, errorMessage, exception))
    }

    fun errorOccurred(error: ApplicationError) {
        applicationErrors.value += error
    }

}