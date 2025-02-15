package net.codinux.accounting.ui.composables.invoice.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.codinux.accounting.domain.invoice.model.CreateInvoiceSettings

class CreateInvoiceSettingsViewModel(settings: CreateInvoiceSettings): ViewModel() {

    private val _showAllSupplierFields = MutableStateFlow(settings.showAllSupplierFields)
    val showAllSupplierFields: StateFlow<Boolean> = _showAllSupplierFields.asStateFlow()

    fun showAllSupplierFieldsChanged(newValue: Boolean) {
        _showAllSupplierFields.value = newValue
    }

    private val _showAllCustomerFields = MutableStateFlow(settings.showAllCustomerFields)
    val showAllCustomerFields: StateFlow<Boolean> = _showAllCustomerFields.asStateFlow()

    fun showAllCustomerFieldsChanged(newValue: Boolean) {
        _showAllCustomerFields.value = newValue
    }

    private val _showAllBankDetailsFields = MutableStateFlow(settings.showAllBankDetailsFields)
    val showAllBankDetailsFields: StateFlow<Boolean> = _showAllBankDetailsFields.asStateFlow()

    fun showAllBankDetailsFieldsChanged(newValue: Boolean) {
        _showAllBankDetailsFields.value = newValue
    }


    private val _lastXmlSaveDirectory = MutableStateFlow(settings.lastXmlSaveDirectory)
    val lastXmlSaveDirectory: StateFlow<String?> = _lastXmlSaveDirectory.asStateFlow()

    fun lastXmlSaveDirectoryChanged(newValue: String?) {
        _lastXmlSaveDirectory.value = newValue
    }

    private val _lastPdfSaveDirectory = MutableStateFlow(settings.lastPdfSaveDirectory)
    val lastPdfSaveDirectory: StateFlow<String?> = _lastPdfSaveDirectory.asStateFlow()

    fun lastPdfSaveDirectoryChanged(newValue: String?) {
        _lastPdfSaveDirectory.value = newValue
    }

    private val _lastOpenFileDirectory = MutableStateFlow(settings.lastOpenFileDirectory)
    val lastOpenFileDirectory: StateFlow<String?> = _lastOpenFileDirectory.asStateFlow()

    fun lastOpenFileDirectoryChanged(newValue: String?) {
        _lastOpenFileDirectory.value = newValue
    }

}