package net.codinux.accounting.domain.common.service

import net.codinux.accounting.domain.common.model.localization.DisplayName
import net.codinux.accounting.domain.common.model.localization.MayUntranslatedDisplayName
import net.codinux.accounting.resources.*
import net.codinux.i18n.DisplayNames
import net.codinux.i18n.unit.UnitFormatStyle
import net.codinux.i18n.unit.UnitFormatter
import net.codinux.invoicing.model.codes.Country
import net.codinux.invoicing.model.codes.Currency
import net.codinux.invoicing.model.codes.UnitOfMeasure
import org.jetbrains.compose.resources.StringResource

class LocalizationService {

    private val displayNames = DisplayNames()

    private val unitFormatter = UnitFormatter()


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

    fun getAllUnitDisplayNames(): List<MayUntranslatedDisplayName<UnitOfMeasure>> {
        return UnitOfMeasure.entries.map { unit -> getUnitDisplayName(unit) }
    }

    fun getUnitDisplayName(unit: UnitOfMeasure): MayUntranslatedDisplayName<UnitOfMeasure> =
        MayUntranslatedDisplayName(unit, unitFormatter.cleanAndGetUnitDisplayName(unit.englishName, UnitFormatStyle.Long) ?: unit.englishName, // TODO: what else to use as fallback value?
            getDisplayNameForUntranslatedUnit(unit))

    private fun getDisplayNameForUntranslatedUnit(unit: UnitOfMeasure): StringResource? = when (unit) {
        UnitOfMeasure.H87 -> Res.string.unit_piece
        UnitOfMeasure.NAR -> Res.string.unit_number_of_articles
        UnitOfMeasure.C62 -> Res.string.unit_unit
        UnitOfMeasure.PR -> Res.string.unit_pair
        UnitOfMeasure.SET -> Res.string.unit_set
        UnitOfMeasure.ZZ -> Res.string.unit_mutually_defined
        else -> null
    }

}