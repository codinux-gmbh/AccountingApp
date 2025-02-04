package net.codinux.accounting.ui.composables.invoice.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*
import net.codinux.accounting.domain.invoice.model.ServiceDateOptions
import net.codinux.invoicing.model.Invoice
import net.codinux.invoicing.model.LocalDate
import net.codinux.invoicing.model.ServiceDate
import net.codinux.invoicing.model.codes.Currency

class DescriptionOfServicesViewModel(selectedServiceDateOption: ServiceDateOptions, invoice: Invoice?) : ViewModel() {

    private val _serviceDateOption = MutableStateFlow(selectedServiceDateOption)
    val serviceDateOption: StateFlow<ServiceDateOptions> = _serviceDateOption.asStateFlow()

    fun serviceDateOptionChanged(newValue: ServiceDateOptions) {
        _serviceDateOption.value = newValue
    }

    private val _serviceDate = MutableStateFlow(invoice?.details?.serviceDate ?: ServiceDate.DeliveryDate(LocalDate.now()))
    val serviceDate: StateFlow<ServiceDate> = _serviceDate.asStateFlow()

    fun serviceDateChanged(newValue: ServiceDate) {
        _serviceDate.value = newValue
    }

    private val _currency = MutableStateFlow(invoice?.details?.currency ?: Currency.Euro) // TODO: get user's default currency
    val currency: StateFlow<Currency> = _currency.asStateFlow()

    fun currencyChanged(newValue: Currency) {
        _currency.value = newValue
    }


    private val _items = MutableStateFlow(invoice?.items?.map { InvoiceItemViewModel(it) }?.toMutableList() ?: mutableListOf(InvoiceItemViewModel()))
    val items: StateFlow<List<InvoiceItemViewModel>> = _items.asStateFlow()

    fun itemAdded(newItem: InvoiceItemViewModel) {
        _items.value = (items.value + newItem).toMutableList() // create a new list, otherwise .collectAsState() will not fire
    }

    fun removeItem(item: InvoiceItemViewModel) {
        _items.value = items.value.toMutableList()
            .apply {
                remove(item)
                if (this.isEmpty()) {
                    add(InvoiceItemViewModel())
                }
            }
    }

}