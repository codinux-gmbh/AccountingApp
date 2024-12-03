package net.codinux.accounting.ui.composables.invoice.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.codinux.invoicing.model.BankDetails

class BankDetailsViewModel(bankDetails: BankDetails?) : ViewModel() {

    private val _accountHolderName = MutableStateFlow(bankDetails?.accountHolderName ?: "")
    val accountHolderName: StateFlow<String> = _accountHolderName.asStateFlow()

    fun accountHolderNameChanged(newValue: String) {
        _accountHolderName.value = newValue
    }

    private val _bankName = MutableStateFlow(bankDetails?.financialInstitutionName ?: "")
    val bankName: StateFlow<String> = _bankName.asStateFlow()

    fun bankNameChanged(newValue: String) {
        _bankName.value = newValue
    }

    private val _accountNumber = MutableStateFlow(bankDetails?.accountNumber ?: "")
    val accountNumber: StateFlow<String> = _accountNumber.asStateFlow()

    fun accountNumberChanged(newValue: String) {
        _accountNumber.value = newValue
    }

    private val _bankCode = MutableStateFlow(bankDetails?.bankCode ?: "")
    val bankCode: StateFlow<String> = _bankCode.asStateFlow()

    fun bankCodeChanged(newValue: String) {
        _bankCode.value = newValue
    }

}