package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.codinux.accounting.domain.invoice.model.ServiceDateOptions
import net.codinux.accounting.platform.Platform
import net.codinux.accounting.platform.isCompactScreen
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.forms.Section
import net.codinux.accounting.ui.composables.forms.Select
import net.codinux.accounting.ui.composables.forms.datetime.DatePicker
import net.codinux.accounting.ui.composables.forms.datetime.SelectMonth
import net.codinux.accounting.ui.composables.invoice.model.*
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.config.Style
import net.codinux.accounting.ui.extensions.widthForScreen
import org.jetbrains.compose.resources.stringResource
import java.time.LocalDate


private val VerticalRowPadding = Style.FormVerticalRowPadding

@Composable
fun InvoiceForm() {

    val historicalData = DI.uiState.historicalInvoiceData.collectAsState().value


    val details by remember(historicalData) { mutableStateOf(InvoiceDetailsViewModel(historicalData.lastCreatedInvoice?.details)) }

    val supplier by remember(historicalData) { mutableStateOf(PartyViewModel(historicalData.lastCreatedInvoice?.supplier)) }

    val customer by remember(historicalData) { mutableStateOf(PartyViewModel(historicalData.lastCreatedInvoice?.customer)) }

    val bankDetails by remember(historicalData) { mutableStateOf(BankDetailsViewModel(historicalData.lastCreatedInvoice?.supplier?.bankDetails)) }

    val servicePeriodDefaultMonth = LocalDate.now().minusMonths(1)
    var selectedServiceDateOption by remember(historicalData) { mutableStateOf(historicalData.selectedServiceDateOption) }
    var serviceDate by remember { mutableStateOf(LocalDate.now()) }
    var servicePeriodMonth by remember { mutableStateOf(servicePeriodDefaultMonth.month) }
    var servicePeriodStart by remember { mutableStateOf(servicePeriodDefaultMonth.withDayOfMonth(1)) }
    var servicePeriodEnd by remember { mutableStateOf(servicePeriodDefaultMonth.withDayOfMonth(servicePeriodDefaultMonth.lengthOfMonth())) }

    val invoiceItems: MutableList<InvoiceItemViewModel> = remember(historicalData) { mutableStateListOf(
        *(historicalData.lastCreatedInvoice?.items?.map { InvoiceItemViewModel(it) }?.toTypedArray() ?: arrayOf(InvoiceItemViewModel()))
    ) }

    val isCompactScreen = Platform.isCompactScreen


    @Composable
    fun getLabel(option: ServiceDateOptions): String = when (option) {
        ServiceDateOptions.DeliveryDate -> stringResource(Res.string.delivery_date)
        ServiceDateOptions.ServiceDate -> stringResource(Res.string.service_date)
        ServiceDateOptions.ServicePeriodMonth -> stringResource(Res.string.service_period_month)
        ServiceDateOptions.ServicePeriodCustom -> stringResource(Res.string.service_period)
    }


    Column(Modifier.fillMaxWidth()) {
        Section(Res.string.invoice_details) {
            InvoiceDetailsForm(details, isCompactScreen)
        }

        Section(Res.string.supplier) {
            InvoicePartyForm(supplier, true, isCompactScreen)
        }

        Section(Res.string.customer) {
            InvoicePartyForm(customer, false, isCompactScreen)
        }

        Section(Res.string.description_of_services) {
            Row(Modifier.fillMaxWidth().padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
                Select(null, ServiceDateOptions.entries, selectedServiceDateOption, { selectedServiceDateOption = it }, { getLabel(it) }, Modifier.padding(end = 8.dp).widthForScreen(isCompactScreen, 185.dp, 210.dp))

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

            Column(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(top = 12.dp).height(30.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(Res.string.delivered_goods_or_provided_services), fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)

                    Spacer(Modifier.weight(1f))

                    TextButton({ invoiceItems.add(InvoiceItemViewModel()) }, contentPadding = PaddingValues(0.dp)) {
                        Icon(Icons.Outlined.Add, "Add invoice item", Modifier.width(48.dp).fillMaxHeight(), Colors.CodinuxSecondaryColor)
                    }
                }

                invoiceItems.forEach { item ->
                    InvoiceItemForm(item)
                }
            }
        }

        Section(Res.string.bank_details) {
            BankDetailsForm(bankDetails)
        }

        Section(Res.string.create) {
            CreateInvoiceForm(historicalData, details, supplier, customer, selectedServiceDateOption, invoiceItems, bankDetails)
        }

        Spacer(Modifier.padding(bottom = Style.MainScreenTabVerticalPadding))
    }
}