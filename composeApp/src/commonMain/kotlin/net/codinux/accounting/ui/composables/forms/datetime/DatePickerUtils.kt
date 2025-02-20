package net.codinux.accounting.ui.composables.forms.datetime


import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.yearcalendar.YearCalendarLayoutInfo
import com.kizitonwose.calendar.compose.yearcalendar.YearCalendarState
import com.kizitonwose.calendar.core.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.datetime.DateTimeUnit

fun Modifier.clickableWithRipple(
    enabled: Boolean = true,
    showRipple: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = if (showRipple) LocalIndication.current else null,
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick,
    )
}

/**
 * Alternative way to find the first fully visible month in the layout.
 *
 * @see [rememberFirstVisibleMonthAfterScroll]
 * @see [rememberFirstMostVisibleMonth]
 */
@Composable
fun rememberFirstCompletelyVisibleMonth(state: CalendarState): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    // Only take non-null values as null will be produced when the
    // list is mid-scroll as no index will be completely visible.
    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo.completelyVisibleMonths.firstOrNull() }
            .filterNotNull()
            .collect { month -> visibleMonth.value = month }
    }
    return visibleMonth.value
}

/**
 * Returns the first visible month in a paged calendar **after** scrolling stops.
 *
 * @see [rememberFirstCompletelyVisibleMonth]
 * @see [rememberFirstMostVisibleMonth]
 */
@Composable
fun rememberFirstVisibleMonthAfterScroll(state: CalendarState): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .filter { scrolling -> !scrolling }
            .collect { visibleMonth.value = state.firstVisibleMonth }
    }
    return visibleMonth.value
}

/**
 * Find the first month on the calendar visible up to the given [viewportPercent] size.
 *
 * @see [rememberFirstCompletelyVisibleMonth]
 * @see [rememberFirstVisibleMonthAfterScroll]
 */
@Composable
fun rememberFirstMostVisibleMonth(
    state: CalendarState,
    viewportPercent: Float = 50f,
): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo.firstMostVisibleMonth(viewportPercent) }
            .filterNotNull()
            .collect { month -> visibleMonth.value = month }
    }
    return visibleMonth.value
}

/**
 * Find the first year on the calendar visible up to the given [viewportPercent] size.
 *
 * @see [rememberFirstVisibleYearAfterScroll]
 */
@Composable
fun rememberFirstMostVisibleYear(
    state: YearCalendarState,
    viewportPercent: Float = 50f,
): CalendarYear {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleYear) }
    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo.firstMostVisibleYear(viewportPercent) }
            .filterNotNull()
            .collect { month -> visibleMonth.value = month }
    }
    return visibleMonth.value
}

/**
 * Returns the first visible year in a paged calendar **after** scrolling stops.
 *
 * @see [rememberFirstMostVisibleYear]
 */
@Composable
fun rememberFirstVisibleYearAfterScroll(state: YearCalendarState): CalendarYear {
    val visibleYear = remember(state) { mutableStateOf(state.firstVisibleYear) }
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .filter { scrolling -> !scrolling }
            .collect { visibleYear.value = state.firstVisibleYear }
    }
    return visibleYear.value
}

private val CalendarLayoutInfo.completelyVisibleMonths: List<CalendarMonth>
    get() {
        val visibleItemsInfo = this.visibleMonthsInfo.toMutableList()
        return if (visibleItemsInfo.isEmpty()) {
            emptyList()
        } else {
            val lastItem = visibleItemsInfo.last()
            val viewportSize = this.viewportEndOffset + this.viewportStartOffset
            if (lastItem.offset + lastItem.size > viewportSize) {
                visibleItemsInfo.removeLast()
            }
            val firstItem = visibleItemsInfo.firstOrNull()
            if (firstItem != null && firstItem.offset < this.viewportStartOffset) {
                visibleItemsInfo.removeFirst()
            }
            visibleItemsInfo.map { it.month }
        }
    }

private fun CalendarLayoutInfo.firstMostVisibleMonth(viewportPercent: Float = 50f): CalendarMonth? {
    return if (visibleMonthsInfo.isEmpty()) {
        null
    } else {
        val viewportSize = (viewportEndOffset + viewportStartOffset) * viewportPercent / 100f
        visibleMonthsInfo.firstOrNull { itemInfo ->
            if (itemInfo.offset < 0) {
                itemInfo.offset + itemInfo.size >= viewportSize
            } else {
                itemInfo.size - itemInfo.offset >= viewportSize
            }
        }?.month
    }
}

private fun YearCalendarLayoutInfo.firstMostVisibleYear(viewportPercent: Float = 50f): CalendarYear? {
    return if (visibleYearsInfo.isEmpty()) {
        null
    } else {
        val viewportSize = (viewportEndOffset + viewportStartOffset) * viewportPercent / 100f
        visibleYearsInfo.firstOrNull { itemInfo ->
            if (itemInfo.offset < 0) {
                itemInfo.offset + itemInfo.size >= viewportSize
            } else {
                itemInfo.size - itemInfo.offset >= viewportSize
            }
        }?.year
    }
}

val YearMonth.next: YearMonth get() = this.plus(1, DateTimeUnit.MONTH)
val YearMonth.previous: YearMonth get() = this.minus(1, DateTimeUnit.MONTH)