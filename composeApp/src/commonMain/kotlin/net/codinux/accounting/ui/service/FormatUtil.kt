package net.codinux.accounting.ui.service

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class FormatUtil {

    companion object {
        private val DayAndMonthDateFormat = DateTimeFormatter.ofPattern("dd. MMM")
    }


    fun formatDateToDayAndMonth(date: LocalDate): String =
        DayAndMonthDateFormat.format(date)

    fun formatDateToDayAndMonth(instant: Instant): String =
        formatDateToDayAndMonth(toDate(instant))


    private fun toDate(instant: Instant): LocalDate =
        instant.atZone(ZoneId.systemDefault()).toLocalDate()

}