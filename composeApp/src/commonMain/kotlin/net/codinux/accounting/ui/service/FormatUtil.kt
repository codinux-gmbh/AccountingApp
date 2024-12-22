package net.codinux.accounting.ui.service

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import kotlinx.datetime.number
import net.codinux.accounting.domain.common.extensions.toEInvoicingDate
import net.codinux.accounting.domain.common.extensions.toI18nDate
import net.codinux.i18n.LanguageTag
import net.codinux.i18n.datetime.DateFieldWidth
import net.codinux.i18n.datetime.DateTimeFormatter
import net.codinux.i18n.datetime.WeekDayStyle
import net.codinux.invoicing.model.*
import net.codinux.invoicing.model.codes.Currency
import net.codinux.log.Log
import java.text.DecimalFormat
import java.text.NumberFormat

class FormatUtil(
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter()
) {

    companion object {
        val DayAndMonthDateFormatPattern = "dd. MMM"


        private val CurrencyFormat = DecimalFormat.getCurrencyInstance()
        private val CurrencyFormatForCustomCurrency = DecimalFormat.getCurrencyInstance()

        private val CurrencyFormatWithoutDecimalPlaces = DecimalFormat.getCurrencyInstance().apply {
            maximumFractionDigits = 0
        }
        private val CurrencyFormatWithoutDecimalPlacesForCustomCurrency = DecimalFormat.getCurrencyInstance().apply {
            maximumFractionDigits = 0
        }

        private val PercentageFormat = DecimalFormat.getPercentInstance()
    }


    fun formatDateToDayAndMonth(date: LocalDate): String =
        dateTimeFormatter.formatDate(date.toI18nDate(), DayAndMonthDateFormatPattern)

    fun formatDateToDayAndMonth(instant: Instant): String =
        formatDateToDayAndMonth(toDate(instant))


    fun formatShortDate(date: kotlinx.datetime.LocalDate): String =
        formatShortDate(date.toEInvoicingDate())

    fun formatShortDate(date: LocalDate): String =
        dateTimeFormatter.formatDate(date.toI18nDate(), net.codinux.i18n.datetime.FormatStyle.Short)


    fun getMonthName(month: Month): String =
        formatMonth(month, false)

    fun formatMonth(month: Month, short: Boolean = true, locale: LanguageTag = LanguageTag.current): String {
        val style = if (short) DateFieldWidth.Abbreviated else DateFieldWidth.Wide

        return dateTimeFormatter.format(net.codinux.i18n.datetime.Month.entries.first { it.monthNumber == month.number }, style, locale)
    }

    fun formatDay(day: DayOfWeek, uppercase: Boolean = false, narrow: Boolean = false, locale: LanguageTag = LanguageTag.current): String {
        val style = if (narrow) WeekDayStyle.Narrow else WeekDayStyle.Short

        return dateTimeFormatter.format(net.codinux.i18n.datetime.DayOfWeek.entries.first { it.ordinal == day.ordinal }, style, locale).let { value ->
            if (uppercase) value.uppercase() else value
        }
    }


    private fun toDate(instant: Instant): LocalDate =
        instant.toLocalDateAtSystemDefaultZone()


    fun formatAmountOfMoney(amount: BigDecimal, currency: Currency? = null, ignoreEmptyDecimalPlacesForLargerAmounts: Boolean = false): String =
        formatAmountOfMoney(amount, currency?.alpha3Code, ignoreEmptyDecimalPlacesForLargerAmounts)

    fun formatAmountOfMoney(amount: BigDecimal, currencyIsoCode: String? = null, ignoreEmptyDecimalPlacesForLargerAmounts: Boolean = false): String =
        if (ignoreEmptyDecimalPlacesForLargerAmounts && hasDecimalPlaces(amount) == false && amount.compareTo(BigDecimal(1_000)) >= 0) {
            getForCurrency(CurrencyFormatWithoutDecimalPlaces, CurrencyFormatWithoutDecimalPlacesForCustomCurrency, currencyIsoCode).format(amount.toJvmBigDecimal())
        } else {
            getForCurrency(CurrencyFormat, CurrencyFormatForCustomCurrency, currencyIsoCode).format(amount.toJvmBigDecimal())
        }

    private fun getForCurrency(currencyFormat: NumberFormat, currencyFormatForCustomCurrency: NumberFormat, currencyCode: String?): NumberFormat {
        val currency = try {
            currencyCode?.let { java.util.Currency.getInstance(currencyCode) }
        } catch (_: Throwable) {
            if (currencyCode != null && currencyCode.length < 3) { // then currencyIsoCode is probably the currency symbol
                Currency.entries.firstOrNull { it.currencySymbol == currencyCode }?.let {
                    java.util.Currency.getInstance(it.alpha3Code)
                }
            } else {
                Log.warn { "Could not map currency ISO code '$currencyCode' to Currency" }
                null
            }
        }

        return if (currency != null) {
            currencyFormatForCustomCurrency.apply {
                this.currency = currency
            }
        } else {
            currencyFormat
        }
    }

    fun formatPercentage(percentage: BigDecimal): String =
        PercentageFormat.format(percentage.toJvmBigDecimal().divide(java.math.BigDecimal(100)))

    fun formatQuantity(quantity: BigDecimal): String =
        quantity.setScale(getCountDecimalPlaces(quantity)).toPlainString()

    private fun hasDecimalPlaces(value: BigDecimal): Boolean =
        value.toJvmBigDecimal().stripTrailingZeros().scale() < value.toJvmBigDecimal().scale()

    private fun getCountDecimalPlaces(value: BigDecimal): Int =
        value.toJvmBigDecimal().stripTrailingZeros().scale()

}