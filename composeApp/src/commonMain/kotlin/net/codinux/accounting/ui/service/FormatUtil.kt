package net.codinux.accounting.ui.service

import net.codinux.invoicing.model.*
import net.codinux.invoicing.model.codes.Currency
import net.codinux.log.Log
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class FormatUtil {

    companion object {
        val DayAndMonthDateFormat = DateTimeFormatter.ofPattern("dd. MMM")

        val ShortDateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)


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


    fun formatShortDate(date: LocalDate): String =
        ShortDateFormat.format(date.toJvmDate())

    fun formatShortDate(instant: Instant): String =
        formatShortDate(toDate(instant))


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