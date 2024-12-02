package net.codinux.accounting.ui.state

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import net.codinux.accounting.domain.common.model.error.ApplicationError
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.invoice.model.HistoricalInvoiceData
import net.codinux.accounting.ui.tabs.MainScreenTab
import org.jetbrains.compose.resources.StringResource

class UiState : ViewModel() {

    val selectedMainScreenTab = MutableStateFlow(MainScreenTab.Mails)


    val historicalInvoiceData = MutableStateFlow(HistoricalInvoiceData())


    val emails = EmailsUiState()


    val applicationErrors = MutableStateFlow<List<ApplicationError>>(emptyList())

    fun errorOccurred(erroneousAction: ErroneousAction, errorMessage: StringResource, exception: Throwable? = null, vararg errorMessageArguments: Any) {
        errorOccurred(ApplicationError(erroneousAction, errorMessage, exception, errorMessageArguments.toList()))
    }

    fun errorOccurred(error: ApplicationError) {
        applicationErrors.value += error
    }

}