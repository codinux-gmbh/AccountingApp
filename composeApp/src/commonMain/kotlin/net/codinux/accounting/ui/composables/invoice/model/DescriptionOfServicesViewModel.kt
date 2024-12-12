package net.codinux.accounting.ui.composables.invoice.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.codinux.accounting.domain.invoice.model.ServiceDateOptions
import net.codinux.invoicing.model.Invoice
import net.codinux.invoicing.model.codes.Currency

class DescriptionOfServicesViewModel(selectedServiceDateOption: ServiceDateOptions, invoice: Invoice?) : ViewModel() {

    private val _serviceDateOption = MutableStateFlow(selectedServiceDateOption)
    val serviceDateOption: StateFlow<ServiceDateOptions> = _serviceDateOption.asStateFlow()

    fun serviceDateOptionChanged(newValue: ServiceDateOptions) {
        _serviceDateOption.value = newValue
    }

    private val _currency = MutableStateFlow(invoice?.details?.currency ?: Currency.EUR) // TODO: get user's default currency
    val currency: StateFlow<Currency> = _currency.asStateFlow()

    fun currencyChanged(newValue: Currency) {
        _currency.value = newValue
    }


    private val _items = MutableStateFlow(invoice?.items?.map { InvoiceItemViewModel(it) }?.toMutableList() ?: mutableListOf(InvoiceItemViewModel()))
    val items: StateFlow<List<InvoiceItemViewModel>> = _items.asStateFlow()

    fun itemAdded(newItem: InvoiceItemViewModel) {
        _items.value = (items.value + newItem).toMutableList() // create a new list, otherwise .collectAsState() will not fire
    }

}