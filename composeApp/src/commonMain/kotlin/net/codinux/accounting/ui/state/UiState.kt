package net.codinux.accounting.ui.state

import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import net.codinux.accounting.domain.common.model.error.ApplicationError
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.invoice.model.CreateInvoiceSettings
import net.codinux.accounting.domain.invoice.model.ViewInvoiceSettings
import net.codinux.accounting.ui.tabs.MainScreenTab
import org.jetbrains.compose.resources.StringResource

class UiState : ViewModel() {

    val screenSize = MutableStateFlow(ScreenSizeInfo(0.dp, 0.dp))

    val uiType = MutableStateFlow(UiType.Compact)

    val isCompactScreen: Boolean
        get() = uiType.value.isCompactScreen

    fun screenSizeChanged(screenSize: ScreenSizeInfo) {
        this.screenSize.value = screenSize
        this.uiType.value = screenSize.uiType
    }

    val isKeyboardVisible = MutableStateFlow(false)


    val selectedMainScreenTab = MutableStateFlow(MainScreenTab.CreateInvoice)


    val viewInvoiceSettings = MutableStateFlow(ViewInvoiceSettings())

    val createInvoiceSettings = MutableStateFlow(CreateInvoiceSettings())


    val emails = EmailsUiState()


    val applicationErrors = MutableStateFlow<List<ApplicationError>>(emptyList())

    fun errorOccurred(erroneousAction: ErroneousAction, errorMessage: StringResource, exception: Throwable? = null, vararg errorMessageArguments: Any) {
        errorOccurred(ApplicationError(erroneousAction, errorMessage, exception, errorMessageArguments.toList()))
    }

    fun errorOccurred(error: ApplicationError) {
        applicationErrors.value += error
    }

}