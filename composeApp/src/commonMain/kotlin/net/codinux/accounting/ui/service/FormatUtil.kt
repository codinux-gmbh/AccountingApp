package net.codinux.accounting.ui.service

import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class FormatUtil {

    companion object {
        val DayAndMonthDateFormat = DateTimeFormatter.ofPattern("dd. MMM")

        val ShortDateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)


        private val CurrencyFormat = DecimalFormat.getCurrencyInstance()
        private val CurrencyFormatWithoutDecimalPlaces = DecimalFormat.getCurrencyInstance().apply {
            maximumFractionDigits = 0
        }

        private val PercentageFormat = DecimalFormat.getPercentInstance()
    }


    fun formatDateToDayAndMonth(date: LocalDate): String =
        DayAndMonthDateFormat.format(date)

    fun formatDateToDayAndMonth(instant: Instant): String =
        formatDateToDayAndMonth(toDate(instant))


    fun formatShortDate(date: LocalDate): String =
        ShortDateFormat.format(date)

    fun formatShortDate(instant: Instant): String =
        formatShortDate(toDate(instant))


    private fun toDate(instant: Instant): LocalDate =
        instant.atZone(ZoneId.systemDefault()).toLocalDate()


    fun formatAmountOfMoney(amount: BigDecimal, ignoreEmptyDecimalPlacesForLargerAmounts: Boolean = false): String =
        if (ignoreEmptyDecimalPlacesForLargerAmounts && hasDecimalPlaces(amount) == false && amount.compareTo(BigDecimal(1_000)) >= 0) {
            CurrencyFormatWithoutDecimalPlaces.format(amount)
        } else {
            CurrencyFormat.format(amount)
        }

    fun formatPercentage(percentage: BigDecimal): String =
        PercentageFormat.format(percentage.divide(BigDecimal(100)))

    fun formatQuantity(quantity: BigDecimal): String =
        quantity.setScale(getCountDecimalPlaces(quantity)).toPlainString()

    private fun hasDecimalPlaces(value: BigDecimal): Boolean =
        value.stripTrailingZeros().scale() < value.scale()

    private fun getCountDecimalPlaces(value: BigDecimal): Int =
        value.stripTrailingZeros().scale()

}