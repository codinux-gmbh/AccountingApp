package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.codinux.accounting.domain.invoice.model.CreateInvoiceSettings
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.AvoidCutOffAtEndOfScreen
import net.codinux.accounting.ui.composables.ComposableOfMaxWidth
import net.codinux.accounting.ui.composables.TextOfMaxWidth
import net.codinux.accounting.ui.composables.forms.Section
import net.codinux.accounting.ui.composables.invoice.model.*
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.config.Style
import net.codinux.kotlin.Platform
import net.codinux.kotlin.PlatformType

@Composable
fun InvoiceForm() {

    val settings = DI.uiState.createInvoiceSettings.collectAsState().value

    val lastCreatedInvoice = settings.lastCreatedInvoice

    val coroutineScope = rememberCoroutineScope()


    fun invoiceDataChanged(settings: CreateInvoiceSettings) {
        coroutineScope.launch {
            DI.invoiceService.saveCreateInvoiceSettings(settings)
        }
    }


    val details by remember(settings) { mutableStateOf(InvoiceDetailsViewModel(lastCreatedInvoice?.details)) }

    val supplier by remember(settings) { mutableStateOf(PartyViewModel(lastCreatedInvoice?.supplier)) }

    val customer by remember(settings) { mutableStateOf(PartyViewModel(lastCreatedInvoice?.customer)) }

    val descriptionOfServices by remember(settings) { mutableStateOf(DescriptionOfServicesViewModel(settings.selectedServiceDateOption, lastCreatedInvoice)) }

    val bankDetails by remember(settings) { mutableStateOf(BankDetailsViewModel(lastCreatedInvoice?.supplier?.bankDetails)) }

    val isCompactScreen = DI.uiState.uiType.collectAsState().value.isCompactScreen


    ComposableOfMaxWidth {
        Column(Modifier.fillMaxWidth()) {
            if (Platform.type != PlatformType.iOS) {
                TextOfMaxWidth(Res.string.notification_early_preview_version, Modifier.padding(top = Style.SectionTopPadding * 2, bottom = Style.SectionTopPadding), Colors.HighlightedTextColor)
            }

            Section(Res.string.invoice_details, true) {
                InvoiceDetailsForm(details)
            }

            Section(Res.string.supplier, true, additionalElementAtEnd = { toggleShowAllFields(settings.showAllSupplierFields) {
                invoiceDataChanged(settings.copy(showAllSupplierFields = it)) } }
            ) {
                InvoicePartyForm(supplier, true, isCompactScreen, settings.showAllSupplierFields)
            }

            Section(Res.string.customer, true, additionalElementAtEnd = { toggleShowAllFields(settings.showAllCustomerFields) {
                invoiceDataChanged(settings.copy(showAllCustomerFields = it)) } }
            ) {
                InvoicePartyForm(customer, false, isCompactScreen, settings.showAllCustomerFields)
            }

            Section(Res.string.description_of_services) {
                DescriptionOfServicesForm(descriptionOfServices, isCompactScreen)
            }

            Section(Res.string.bank_details, true, additionalElementAtEnd = { toggleShowAllFields(settings.showAllBankDetailsFields) {
                invoiceDataChanged(settings.copy(showAllBankDetailsFields = it)) } }
            ) {
                BankDetailsForm(bankDetails, settings.showAllBankDetailsFields)
            }

            Section(Res.string.create) {
                CreateInvoiceForm(settings, details, supplier, customer, descriptionOfServices, bankDetails, isCompactScreen)
            }

            AvoidCutOffAtEndOfScreen()
        }
    }
}

@Composable
fun toggleShowAllFields(showAllFields: Boolean, showAllFieldsChanged: (Boolean) -> Unit) {

    TextButton({ showAllFieldsChanged(!showAllFields) }, Modifier.height(28.dp).width(36.dp), contentPadding = PaddingValues(0.dp)) {
        Column(Modifier.fillMaxSize(), Arrangement.Center, horizontalAlignment = Alignment.End) {
            Icon(if (showAllFields) Icons.Outlined.ArrowDropUp else Icons.Outlined.ArrowDropDown, "Toggle show all or only most common fields",
                Modifier.size(24.dp), tint = Colors.FormValueTextColor)
        }
    }
}
