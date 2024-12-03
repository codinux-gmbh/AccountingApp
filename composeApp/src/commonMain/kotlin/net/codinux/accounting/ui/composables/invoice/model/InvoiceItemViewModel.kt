package net.codinux.accounting.ui.composables.invoice.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.codinux.invoicing.model.InvoiceItem
import java.math.BigDecimal

class InvoiceItemViewModel(item: InvoiceItem? = null) : ViewModel() {

    private val _name = MutableStateFlow(item?.name ?: "")
    val name: StateFlow<String> = _name.asStateFlow()

    fun nameChanged(newValue: String) {
        _name.value = newValue
        validate()
    }

    private val _quantity = MutableStateFlow(item?.quantity)
    val quantity: StateFlow<BigDecimal?> = _quantity.asStateFlow()

    fun quantityChanged(newValue: BigDecimal) {
        _quantity.value = newValue
        validate()
    }

    private val _unit = MutableStateFlow(item?.unit ?: "")
    val unit: StateFlow<String> = _unit.asStateFlow()

    fun unitChanged(newValue: String) {
        _unit.value = newValue
        validate()
    }

    private val _unitPrice = MutableStateFlow(item?.unitPrice)
    val unitPrice: StateFlow<BigDecimal?> = _unitPrice.asStateFlow()

    fun unitPriceChanged(newValue: BigDecimal) {
        _unitPrice.value = newValue
        validate()
    }

    private val _vatRate = MutableStateFlow(item?.vatRate)
    val vatRate: StateFlow<BigDecimal?> = _vatRate.asStateFlow()

    fun vatRateChanged(newValue: BigDecimal) {
        _vatRate.value = newValue
        validate()
    }

    private val _description = MutableStateFlow(item?.description ?: "")
    val description: StateFlow<String> = _description.asStateFlow()

    fun descriptionChanged(newValue: String) {
        _description.value = newValue
        validate()
    }



    private val _isValid = MutableStateFlow(false)
    val isValid: StateFlow<Boolean> get() = _isValid


    fun validate() {
        _isValid.value = _name.value.isNotBlank() && isValid(_quantity) && _unit.value.isNotBlank() && isValid(_unitPrice) && _vatRate.value != null // vatRate may be zero
    }

    private fun isValid(bigDecimal: MutableStateFlow<BigDecimal?>): Boolean =
        bigDecimal.value != null && bigDecimal.value != BigDecimal.ZERO


    init {
        validate() // on initialize check if entered historical data are enough so that data is valid
    }
}