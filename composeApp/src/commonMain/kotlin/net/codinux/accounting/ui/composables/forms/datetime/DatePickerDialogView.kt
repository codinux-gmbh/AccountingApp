package net.codinux.accounting.ui.composables.forms.datetime

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import net.codinux.accounting.ui.config.Colors
import net.codinux.invoicing.model.LocalDate
import net.codinux.invoicing.model.toEInvoicingDate
import java.util.*

// copied from: https://github.com/kizitonwose/Calendar/blob/64b7adbce7c6c7581895575a359fcc8e5d188416/compose-multiplatform/sample/src/commonMain/kotlin/Utils.kt
@Composable
fun DatePickerDialogView(selectedDate: LocalDate? = null, adjacentMonths: Int = 500, dateSelected: (LocalDate) -> Unit) {

    val currentMonth = remember { selectedDate?.let { YearMonth(it.year, it.month) } ?: YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(adjacentMonths) }
    val endMonth = remember { currentMonth.plusMonths(adjacentMonths) }
    val selections = remember { mutableStateListOf<CalendarDay>().also {
        if (selectedDate != null) {
            it.add(CalendarDay(selectedDate.toJvmDate().toKotlinLocalDate(), DayPosition.MonthDate))
        }
    } }
    val daysOfWeek = remember { daysOfWeek() }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        val state = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = daysOfWeek.first(),
        )
        val coroutineScope = rememberCoroutineScope()
        val visibleMonth = rememberFirstMostVisibleMonth(state, viewportPercent = 90f)

        CalendarTitle(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            currentMonth = visibleMonth.yearMonth,
            goToPrevious = {
                coroutineScope.launch {
                    state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previous)
                }
            },
            goToNext = {
                coroutineScope.launch {
                    state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.next)
                }
            },
        )

        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                Day(day, isSelected = selections.contains(day)) { clickedDate ->
                    if (selections.contains(clickedDate)) {
                        selections.remove(clickedDate)
                    } else {
                        selections.add(clickedDate)
                    }

                    dateSelected(clickedDate.date.toJavaLocalDate().toEInvoicingDate())
                }
            },
            monthHeader = {
                MonthHeader(daysOfWeek = daysOfWeek)
            },
        )
    }

}

@Composable
private fun MonthHeader(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                text = dayOfWeek.displayText(),
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun Day(day: CalendarDay, isSelected: Boolean, onClick: (CalendarDay) -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f) // This is important for square-sizing!
            .padding(6.dp)
            .clip(CircleShape)
            .background(color = if (isSelected) Colors.CodinuxSecondaryColor else Color.Transparent)
            // Disable clicks on inDates/outDates
            .clickableWithRipple(
                enabled = day.position == DayPosition.MonthDate,
                showRipple = !isSelected,
                onClick = { onClick(day) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        val textColor = when (day.position) {
            // Color.Unspecified will use the default text color from the current theme
            DayPosition.MonthDate -> if (isSelected) Color.White else Color.Unspecified
            DayPosition.InDate, DayPosition.OutDate -> Colors.Disabled
        }
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            fontSize = 14.sp,
        )
    }
}

fun YearMonth.displayText(short: Boolean = false): String {
    return "${month.displayText(short = short)} $year"
}

fun Month.displayText(short: Boolean = true): String {
    return getDisplayName(short, defaultLocale)
}

fun DayOfWeek.displayText(uppercase: Boolean = false, narrow: Boolean = false): String {
    return getDisplayName(narrow, defaultLocale).let { value ->
        if (uppercase) value.uppercase() else value
    }
}

//expect fun Month.getDisplayName(short: Boolean, locale: Locale): String
//
//expect fun DayOfWeek.getDisplayName(narrow: Boolean = false, locale: Locale): String

fun Month.getDisplayName(short: Boolean, locale: Locale): String {
    val style = if (short) java.time.format.TextStyle.SHORT else java.time.format.TextStyle.FULL
    return getDisplayName(style, java.util.Locale.forLanguageTag(locale.toLanguageTag()))
}

fun DayOfWeek.getDisplayName(narrow: Boolean, locale: Locale): String {
    val style = if (narrow) java.time.format.TextStyle.NARROW else java.time.format.TextStyle.SHORT
    return getDisplayName(style, java.util.Locale.forLanguageTag(locale.toLanguageTag()))
}

//private val defaultLocale = Locale("en-US")
private val defaultLocale = Locale.getDefault()