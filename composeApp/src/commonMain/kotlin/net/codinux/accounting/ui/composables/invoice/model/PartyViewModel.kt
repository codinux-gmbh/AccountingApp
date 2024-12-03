package net.codinux.accounting.ui.composables.invoice.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.codinux.invoicing.model.Party

class PartyViewModel(party: Party?) : ViewModel() {

    private val _name = MutableStateFlow(party?.name ?: "")
    val name: StateFlow<String> = _name.asStateFlow()

    fun nameChanged(newValue: String) {
        _name.value = newValue
        validate()
    }

    private val _address = MutableStateFlow(party?.address ?: "")
    val address: StateFlow<String> = _address.asStateFlow()

    fun addressChanged(newValue: String) {
        _address.value = newValue
        validate()
    }

    private val _postalCode = MutableStateFlow(party?.postalCode ?: "")
    val postalCode: StateFlow<String> = _postalCode.asStateFlow()

    fun postalCodeChanged(newValue: String) {
        _postalCode.value = newValue
        validate()
    }

    private val _city = MutableStateFlow(party?.city ?: "")
    val city: StateFlow<String> = _city.asStateFlow()

    fun cityChanged(newValue: String) {
        _city.value = newValue
        validate()
    }


    private val _email = MutableStateFlow(party?.email ?: "")
    val email: StateFlow<String> = _email.asStateFlow()

    fun emailChanged(newValue: String) {
        _email.value = newValue
        validate()
    }

    private val _phone = MutableStateFlow(party?.phone ?: "")
    val phone: StateFlow<String> = _phone.asStateFlow()

    fun phoneChanged(newValue: String) {
        _phone.value = newValue
        validate()
    }


    private val _vatId = MutableStateFlow(party?.vatId ?: "")
    val vatId: StateFlow<String> = _vatId.asStateFlow()

    fun vatIdChanged(newValue: String) {
        _vatId.value = newValue
        validate()
    }


    private val _isValid = MutableStateFlow(false)
    val isValid: StateFlow<Boolean> get() = _isValid


    fun validate() {
        _isValid.value = _name.value.isNotBlank() && _address.value.isNotBlank() && _postalCode.value.isNotBlank() && _city.value.isNotBlank()
        // TODO: country is also a required field. for supplier vatId may is also required. and for XRechnung supplier's contact details are also required
    }


    init {
        validate() // on initialize check if entered historical data are enough so that data is valid
    }

}