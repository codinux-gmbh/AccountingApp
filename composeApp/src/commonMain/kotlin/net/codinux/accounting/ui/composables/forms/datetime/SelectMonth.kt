package net.codinux.accounting.ui.composables.forms.datetime

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import net.codinux.accounting.domain.common.extensions.now
import net.codinux.accounting.resources.Res
import net.codinux.accounting.resources.month
import net.codinux.accounting.ui.composables.forms.Select
import net.codinux.accounting.ui.config.DI


private val formatUtil = DI.formatUtil

@Composable
fun SelectMonth(selectedMonth: Month = LocalDate.now().month, monthSelected: (Month) -> Unit) {

    Select(Res.string.month, Month.entries, selectedMonth, monthSelected, { formatUtil.getMonthName(it) })

}