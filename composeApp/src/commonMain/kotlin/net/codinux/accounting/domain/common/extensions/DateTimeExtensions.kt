package net.codinux.accounting.domain.common.extensions

import kotlinx.datetime.*
import kotlinx.datetime.Month


fun net.codinux.invoicing.model.LocalDate.toKotlinLocalDate() = LocalDate(this.year, this.month, this.dayOfMonth)

fun LocalDate.toEInvoicingDate() = net.codinux.invoicing.model.LocalDate(this.year, this.monthNumber, this.dayOfMonth)

fun net.codinux.invoicing.model.LocalDate.toI18nDate() = net.codinux.i18n.datetime.LocalDate(this.year, this.month, this.dayOfMonth)


fun net.codinux.invoicing.model.Instant.toKotlinInstant() = Instant.fromEpochSeconds(this.epochSeconds, this.nanosecondsOfSecond)

fun Instant.toEInvoiceInstant() = net.codinux.invoicing.model.Instant(this.epochSeconds, this.nanosecondsOfSecond)


fun net.codinux.invoicing.model.LocalDate.toInstantAtSystemDefaultZone(): net.codinux.invoicing.model.Instant =
    this.toKotlinLocalDate().atStartOfDayIn(TimeZone.currentSystemDefault()).toEInvoiceInstant()


fun LocalDate.withDayOfMonth(dayOfMonth: Int) =
    if (dayOfMonth == this.dayOfMonth) {
        this
    } else {
        LocalDate(this.year, this.monthNumber, dayOfMonth)
    }

fun LocalDate.atEndOfMonth() = LocalDate(this.year, this.monthNumber, this.lengthOfMonth())

fun LocalDate.lengthOfMonth(): Int = when (this.month) {
    Month.FEBRUARY -> {
        if (this.year % 4 == 0) 29 // not perfectly fine, e.g. 2100 will not be a leap year, but i think till then this software will not exist anymore
        else 28
    }
    Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
    else -> 31
}


fun LocalDate.Companion.now(): LocalDate =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date