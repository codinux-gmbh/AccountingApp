package net.codinux.accounting.ui.service

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import net.codinux.accounting.domain.common.extensions.toEInvoicingDate
import net.codinux.i18n.LanguageTag
import net.codinux.invoicing.model.*
import net.codinux.invoicing.model.codes.Currency
import net.codinux.log.Log
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class FormatUtil {

    companion object {
        val DayAndMonthDateFormat = DateTimeFormatter.ofPattern("dd. MMM")

        val ShortDateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)


        val LongMonthFormat = DateTimeFormatter.ofPattern("MMMM")


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
        DayAndMonthDateFormat.format(date.toJvmDate())

    fun formatDateToDayAndMonth(instant: Instant): String =
        formatDateToDayAndMonth(toDate(instant))


    fun formatShortDate(date: kotlinx.datetime.LocalDate): String =
        formatShortDate(date.toEInvoicingDate())

    fun formatShortDate(date: LocalDate): String =
        ShortDateFormat.format(date.toJvmDate())

    fun formatShortDate(instant: Instant): String =
        formatShortDate(toDate(instant))


    fun getMonthName(month: Month): String =
        LongMonthFormat.format(java.time.Month.valueOf(month.name))

    fun formatMonth(month: Month, short: Boolean = true, locale: LanguageTag = LanguageTag.current): String {
        val style = if (short) java.time.format.TextStyle.SHORT else java.time.format.TextStyle.FULL

        return month.getDisplayName(style, Locale.forLanguageTag(locale.tag))
    }

    fun formatDay(day: DayOfWeek, uppercase: Boolean = false, narrow: Boolean = false, locale: LanguageTag = LanguageTag.current): String {
        val style = if (narrow) java.time.format.TextStyle.NARROW else java.time.format.TextStyle.SHORT

        return day.getDisplayName(style, Locale.forLanguageTag(locale.tag)).let { value ->
            if (uppercase) value.uppercase() else value
        }
    }


    private fun toDate(instant: Instant): LocalDate =
        instant.toJvmInstant().atZone(ZoneId.systemDefault()).toLocalDate().toEInvoicingDate()


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