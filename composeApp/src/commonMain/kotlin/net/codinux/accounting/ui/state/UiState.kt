package net.codinux.accounting.ui.state

import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import net.codinux.accounting.domain.common.model.error.ApplicationError
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.invoice.model.CreateInvoiceSettings
import net.codinux.accounting.domain.invoice.model.RecentlyViewedInvoice
import net.codinux.accounting.domain.invoice.model.ViewInvoiceSettings
import net.codinux.accounting.domain.ui.model.MainScreenTab
import net.codinux.invoicing.model.dto.SerializableException
import net.codinux.invoicing.pdf.InvoicePdfTemplateSettings
import org.jetbrains.compose.resources.StringResource

class UiState : ViewModel() {

    val screenSize = MutableStateFlow(ScreenSizeInfo(0.dp, 0.dp))

    val uiType = MutableStateFlow(UiType.Compact)

    val isCompactScreen: Boolean
        get() = uiType.value.isCompactScreen

    val isKeyboardVisible = MutableStateFlow(false)


    val selectedMainScreenTab = MutableStateFlow(MainScreenTab.ViewInvoice)


    val viewInvoiceSettings = MutableStateFlow(ViewInvoiceSettings())

    val recentlyViewedInvoices = MutableStateFlow<List<RecentlyViewedInvoice>>(emptyList())


    val createInvoiceSettings = MutableStateFlow(CreateInvoiceSettings())

    val invoicePdfTemplateSettings = MutableStateFlow(InvoicePdfTemplateSettings())


    val emails = EmailsUiState()


    val applicationErrors = MutableStateFlow<List<ApplicationError>>(emptyList())

    fun errorOccurred(erroneousAction: ErroneousAction, errorMessage: StringResource, errorMessageArguments: Any? = null) =
        errorOccurred(erroneousAction, errorMessage, null as? SerializableException, errorMessageArguments)

    fun errorOccurred(erroneousAction: ErroneousAction, errorMessage: StringResource, exception: Throwable? = null, errorMessageArguments: Any? = null) =
        errorOccurred(erroneousAction, errorMessage, exception?.let { SerializableException(it) }, errorMessageArguments)

    fun errorOccurred(erroneousAction: ErroneousAction, errorMessage: StringResource, exception: SerializableException? = null, errorMessageArguments: Any? = null) =
        errorOccurred(ApplicationError(erroneousAction, errorMessage, exception, errorMessageArguments?.let { listOf(it) }.orEmpty()))

    fun errorOccurred(error: ApplicationError) {
        applicationErrors.value += error
    }

}