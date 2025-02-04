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
import com.kizitonwose.calendar.core.minusMonths
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import net.codinux.accounting.domain.common.extensions.*
import net.codinux.accounting.domain.invoice.model.ServiceDateOptions
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.forms.Select
import net.codinux.accounting.ui.composables.forms.datetime.DatePicker
import net.codinux.accounting.ui.composables.forms.datetime.SelectMonth
import net.codinux.accounting.ui.composables.invoice.model.DescriptionOfServicesViewModel
import net.codinux.accounting.ui.extensions.widthForScreen
import net.codinux.invoicing.model.ServiceDate
import org.jetbrains.compose.resources.stringResource

@Composable
fun ServiceDateForm(viewModel: DescriptionOfServicesViewModel, isCompactScreen: Boolean) {

    val selectedServiceDateOption by viewModel.serviceDateOption.collectAsState()
    val serviceDate by viewModel.serviceDate.collectAsState()

    val servicePeriodDefaultMonth = LocalDate.now().let { if (it.dayOfMonth in (it.lengthOfMonth() - 3 .. it.lengthOfMonth())) it else it.minusMonths(1) }
    var deliveryOrServiceDate by remember { mutableStateOf(serviceDate.asDeliveryDate()?.deliveryDate?.toKotlinLocalDate() ?: LocalDate.now()) }
    var servicePeriodMonth by remember { mutableStateOf(servicePeriodDefaultMonth.month) }
    var servicePeriodStart by remember { mutableStateOf(serviceDate.asServicePeriod()?.startDate?.toKotlinLocalDate() ?: servicePeriodDefaultMonth.withDayOfMonth(1)) }
    var servicePeriodEnd by remember { mutableStateOf(serviceDate.asServicePeriod()?.endDate?.toKotlinLocalDate() ?: servicePeriodDefaultMonth.atEndOfMonth()) }


    @Composable
    fun getLabel(option: ServiceDateOptions): String = when (option) {
        ServiceDateOptions.DeliveryDate -> stringResource(Res.string.delivery_date)
        ServiceDateOptions.ServiceDate -> stringResource(Res.string.service_date)
        ServiceDateOptions.ServicePeriodMonth -> stringResource(Res.string.service_period_month)
        ServiceDateOptions.ServicePeriodCustom -> stringResource(Res.string.service_period)
    }

    fun deliveryOrServiceDateChanged(date: LocalDate) {
        deliveryOrServiceDate = date
        viewModel.serviceDateChanged(ServiceDate.DeliveryDate(date.toEInvoicingDate()))
    }

    fun servicePeriodMonthChanged(month: Month) {
        servicePeriodMonth = month

        val now = LocalDate.now()
        val monthStart = if (month <= now.month) LocalDate(now.year, month, 1) else LocalDate(now.year - 1, month, 1)
        viewModel.serviceDateChanged(ServiceDate.ServicePeriod(monthStart.toEInvoicingDate(), monthStart.atEndOfMonth().toEInvoicingDate()))
    }

    fun servicePeriodChanged(startDate: LocalDate, endDate: LocalDate) {
        servicePeriodStart = startDate
        servicePeriodEnd = endDate
        viewModel.serviceDateChanged(ServiceDate.ServicePeriod(startDate.toEInvoicingDate(), endDate.toEInvoicingDate()))
    }

    fun serviceDateOptionChanged(selected: ServiceDateOptions) {
        viewModel.serviceDateOptionChanged(selected)

        when (selected) {
            ServiceDateOptions.DeliveryDate, ServiceDateOptions.ServiceDate -> deliveryOrServiceDateChanged(deliveryOrServiceDate)
            ServiceDateOptions.ServicePeriodMonth -> servicePeriodMonthChanged(servicePeriodMonth)
            ServiceDateOptions.ServicePeriodCustom -> servicePeriodChanged(servicePeriodStart, servicePeriodEnd)
        }
    }


    Row(verticalAlignment = Alignment.CenterVertically) {
        Select(null, ServiceDateOptions.entries, selectedServiceDateOption, { serviceDateOptionChanged(it) }, { getLabel(it) }, Modifier.padding(end = 8.dp).widthForScreen(isCompactScreen, 185.dp, 210.dp))

        when (selectedServiceDateOption) {
            ServiceDateOptions.DeliveryDate -> { DatePicker(null, deliveryOrServiceDate) { deliveryOrServiceDateChanged(it) } }
            ServiceDateOptions.ServiceDate -> { DatePicker(null, deliveryOrServiceDate) { deliveryOrServiceDateChanged(it) } }
            ServiceDateOptions.ServicePeriodMonth -> { SelectMonth(servicePeriodMonth) { servicePeriodMonthChanged(it) } }
            ServiceDateOptions.ServicePeriodCustom -> {
                DatePicker(Res.string.service_period_start, servicePeriodStart, moveFocusOnToNextElementOnSelection = false) { servicePeriodChanged(it, servicePeriodEnd) }
                Text("-", textAlign = TextAlign.Center, modifier = Modifier.width(18.dp))
                DatePicker(Res.string.service_period_end, servicePeriodEnd) { servicePeriodChanged(servicePeriodStart, it) }
            }
        }
    }

}