package net.codinux.accounting.ui.service

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class FormatUtil {

    companion object {
        private val ShortDateFormat = DateTimeFormatter.ofPattern("dd. MMM")
    }


    fun formatShortDate(date: LocalDate): String =
        ShortDateFormat.format(date)

    fun formatShortDate(instant: Instant): String =
        formatShortDate(toDate(instant))


    private fun toDate(instant: Instant): LocalDate =
        instant.atZone(ZoneId.systemDefault()).toLocalDate()

}