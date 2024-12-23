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
import net.codinux.i18n.formatter.NumberFormatter
import net.codinux.invoicing.model.*
import net.codinux.invoicing.model.codes.Currency

class FormatUtil(
    private val numberFormatter: NumberFormatter = NumberFormatter(),
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter()
) {

    companion object {
        val DayAndMonthDateFormatPattern = "dd. MMM"
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

    fun formatAmountOfMoney(amount: BigDecimal, currencyIsoCode: String? = null, ignoreEmptyDecimalPlacesForLargerAmounts: Boolean = false): String {
        val currency = net.codinux.i18n.Currency.entries.first { it.alpha3Code == currencyIsoCode || it.symbol == currencyIsoCode } // bug in TextInfoExtractor that extracts e.g. '€' as isoCode
        val amountDouble = amount.toPlainString().toDouble()
        val countFractionalDigitsOverride = if (ignoreEmptyDecimalPlacesForLargerAmounts && amountDouble > 1_000) 0 else null

        return numberFormatter.formatCurrency(amountDouble, currency, countFractionalDigits = countFractionalDigitsOverride)
    }

    fun formatPercentage(percentage: BigDecimal): String =
        numberFormatter.formatPercent(percentage.toPlainString().toDouble() / 100.0)

    fun formatQuantity(quantity: BigDecimal): String =
        quantity.setScale(getCountDecimalPlaces(quantity)).toPlainString()

    private fun getCountDecimalPlaces(value: BigDecimal): Int =
        value.toJvmBigDecimal().stripTrailingZeros().scale()

}