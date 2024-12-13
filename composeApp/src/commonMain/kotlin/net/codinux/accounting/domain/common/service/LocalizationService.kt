package net.codinux.accounting.domain.common.service

import net.codinux.accounting.domain.common.model.DisplayName
import net.codinux.i18n.DisplayNames
import net.codinux.invoicing.model.codes.Country
import net.codinux.invoicing.model.codes.Currency

class LocalizationService {

    private val displayNames = DisplayNames()


    fun getAllCountryDisplayNames(): List<DisplayName<Country>> {
        val names = displayNames.getAllRegionDisplayNamesForLanguage()

        return Country.entries.map { country ->
            DisplayName(country, names?.get(country.alpha2Code) ?: names?.get(country.alpha3Code) ?: country.englishName) // TODO: what else to use as fallback value?
        }
    }

    fun getAllCurrencyDisplayNames(): List<DisplayName<Currency>> {
        val names = displayNames.getAllCurrencyDisplayNamesForLanguage()

        return Currency.entries.map { currency ->
            DisplayName(currency, names?.get(currency.alpha3Code) ?: currency.englishName) // TODO: what else to use as fallback value?
        }
    }

}