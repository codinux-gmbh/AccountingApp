package net.codinux.accounting.ui.composables.forms.datetime

import androidx.compose.runtime.Composable
import net.codinux.accounting.resources.Res
import net.codinux.accounting.resources.month
import net.codinux.accounting.ui.composables.forms.Select
import org.jetbrains.compose.resources.stringResource
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter

private val monthFormat = DateTimeFormatter.ofPattern("MMMM")

@Composable
fun SelectMonth(selectedMonth: Month = LocalDate.now().month, monthSelected: (Month) -> Unit) {

    Select(stringResource(Res.string.month), Month.entries, selectedMonth, monthSelected, { monthFormat.format(it) })

}