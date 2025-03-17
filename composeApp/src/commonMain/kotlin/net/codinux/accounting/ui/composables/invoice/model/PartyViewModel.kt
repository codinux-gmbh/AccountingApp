package net.codinux.accounting.ui.composables.invoice.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*
import net.codinux.invoicing.model.Party
import net.codinux.invoicing.model.codes.Country

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

    private val _additionalAddressLine = MutableStateFlow(party?.additionalAddressLine ?: "")
    val additionalAddressLine: StateFlow<String> = _additionalAddressLine.asStateFlow()

    fun additionalAddressLineChanged(newValue: String) {
        _additionalAddressLine.value = newValue
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

    private val _country = MutableStateFlow(party?.country ?: Country.Germany) // TODO: get users default country // TODO: use English names as Enum names
    val country: StateFlow<Country> = _country.asStateFlow()

    fun countryChanged(newValue: Country) {
        _country.value = newValue
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


    val propertyChanged = combine(listOf<StateFlow<Any>>(name, address, additionalAddressLine, postalCode, city, country, email, phone, vatId)) {
        it.toList()
    }


    private val _isValid = MutableStateFlow(false)
    val isValid: StateFlow<Boolean> get() = _isValid


    fun validate() {
        _isValid.value = _name.value.isNotBlank() && _address.value.isNotBlank() && _postalCode.value.isNotBlank() && _city.value.isNotBlank()
        // TODO: for supplier vatId may is also required. and for XRechnung supplier's contact details are also required
    }


    init {
        validate() // on initialize check if last entered create invoice settings are enough so that data is valid
    }

}