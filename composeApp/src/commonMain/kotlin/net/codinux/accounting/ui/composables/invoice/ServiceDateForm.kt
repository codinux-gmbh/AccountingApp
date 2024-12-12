package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.codinux.accounting.domain.invoice.model.ServiceDateOptions
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.forms.Select
import net.codinux.accounting.ui.composables.forms.datetime.DatePicker
import net.codinux.accounting.ui.composables.forms.datetime.SelectMonth
import net.codinux.accounting.ui.composables.invoice.model.DescriptionOfServicesViewModel
import net.codinux.accounting.ui.extensions.widthForScreen
import org.jetbrains.compose.resources.stringResource
import java.time.LocalDate

@Composable
fun ServiceDateForm(viewModel: DescriptionOfServicesViewModel, isCompactScreen: Boolean) {

    val selectedServiceDateOption by viewModel.serviceDateOption.collectAsState()
    val servicePeriodDefaultMonth = LocalDate.now().minusMonths(1)
    var serviceDate by remember { mutableStateOf(LocalDate.now()) }
    var servicePeriodMonth by remember { mutableStateOf(servicePeriodDefaultMonth.month) }
    var servicePeriodStart by remember { mutableStateOf(servicePeriodDefaultMonth.withDayOfMonth(1)) }
    var servicePeriodEnd by remember { mutableStateOf(servicePeriodDefaultMonth.withDayOfMonth(servicePeriodDefaultMonth.lengthOfMonth())) }


    @Composable
    fun getLabel(option: ServiceDateOptions): String = when (option) {
        ServiceDateOptions.DeliveryDate -> stringResource(Res.string.delivery_date)
        ServiceDateOptions.ServiceDate -> stringResource(Res.string.service_date)
        ServiceDateOptions.ServicePeriodMonth -> stringResource(Res.string.service_period_month)
        ServiceDateOptions.ServicePeriodCustom -> stringResource(Res.string.service_period)
    }


    Row(verticalAlignment = Alignment.CenterVertically) {
        Select(null, ServiceDateOptions.entries, selectedServiceDateOption, { viewModel.serviceDateOptionChanged(it) }, { getLabel(it) }, Modifier.padding(end = 8.dp).widthForScreen(isCompactScreen, 185.dp, 210.dp))

        when (selectedServiceDateOption) {
            ServiceDateOptions.DeliveryDate -> { DatePicker(null, serviceDate) { serviceDate = it } }
            ServiceDateOptions.ServiceDate -> { DatePicker(null, serviceDate) { serviceDate = it } }
            ServiceDateOptions.ServicePeriodMonth -> { SelectMonth(servicePeriodMonth) { servicePeriodMonth = it } }
            ServiceDateOptions.ServicePeriodCustom -> {
                DatePicker(Res.string.service_period_start, servicePeriodStart, moveFocusOnToNextElementOnSelection = false) { servicePeriodStart = it }
                Text("-", textAlign = TextAlign.Center, modifier = Modifier.width(18.dp))
                DatePicker(Res.string.service_period_end, servicePeriodEnd) { servicePeriodEnd = it }
            }
        }
    }

}