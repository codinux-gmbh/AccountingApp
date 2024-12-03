package net.codinux.accounting.ui.composables.invoice.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.codinux.invoicing.model.InvoiceDetails
import java.time.LocalDate

class InvoiceDetailsViewModel(details: InvoiceDetails?) : ViewModel() {

    private val _invoiceNumber = MutableStateFlow(details?.invoiceNumber ?: "")
    val invoiceNumber: StateFlow<String> = _invoiceNumber.asStateFlow()

    fun invoiceNumberChanged(newValue: String) {
        _invoiceNumber.value = newValue
        validate()
    }

    private val _invoiceDate = MutableStateFlow(details?.invoiceDate ?: LocalDate.now())
    val invoiceDate: StateFlow<LocalDate> = _invoiceDate.asStateFlow()

    fun invoiceDateChanged(newValue: LocalDate) {
        _invoiceDate.value = newValue
    }


    private val _isValid = MutableStateFlow(false)
    val isValid: StateFlow<Boolean> get() = _isValid


    fun validate() {
        _isValid.value = _invoiceNumber.value.isNotBlank()
    }


    init {
        validate() // on initialize check if entered historical data are enough so that data is valid
    }

}