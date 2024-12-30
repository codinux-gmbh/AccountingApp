package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import net.codinux.accounting.platform.Platform
import net.codinux.accounting.platform.isCompactScreen
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.AvoidCutOffAtEndOfScreen
import net.codinux.accounting.ui.composables.TextOfMaxWidth
import net.codinux.accounting.ui.composables.forms.Section
import net.codinux.accounting.ui.composables.invoice.model.*
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.config.Style

@Composable
fun InvoiceForm() {

    val historicalData = DI.uiState.historicalInvoiceData.collectAsState().value


    val details by remember(historicalData) { mutableStateOf(InvoiceDetailsViewModel(historicalData.lastCreatedInvoice?.details)) }

    val supplier by remember(historicalData) { mutableStateOf(PartyViewModel(historicalData.lastCreatedInvoice?.supplier)) }

    val customer by remember(historicalData) { mutableStateOf(PartyViewModel(historicalData.lastCreatedInvoice?.customer)) }

    val descriptionOfServices by remember(historicalData) { mutableStateOf(DescriptionOfServicesViewModel(historicalData.selectedServiceDateOption, historicalData.lastCreatedInvoice)) }

    val bankDetails by remember(historicalData) { mutableStateOf(BankDetailsViewModel(historicalData.lastCreatedInvoice?.supplier?.bankDetails)) }

    val isCompactScreen = Platform.isCompactScreen


    Column(Modifier.fillMaxWidth()) {
        TextOfMaxWidth(Res.string.notification_early_preview_version, Modifier.padding(top = Style.SectionTopPadding * 2, bottom = Style.SectionTopPadding), Colors.CodinuxSecondaryColor)

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
            DescriptionOfServicesForm(descriptionOfServices, isCompactScreen)
        }

        Section(Res.string.bank_details) {
            BankDetailsForm(bankDetails)
        }

        Section(Res.string.create) {
            CreateInvoiceForm(historicalData, details, supplier, customer, descriptionOfServices, bankDetails, isCompactScreen)
        }

        AvoidCutOffAtEndOfScreen()
    }
}