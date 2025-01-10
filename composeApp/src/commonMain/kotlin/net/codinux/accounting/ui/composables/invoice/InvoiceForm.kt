package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
                TextOfMaxWidth(Res.string.notification_early_preview_version, Modifier.padding(top = Style.SectionTopPadding * 2, bottom = Style.SectionTopPadding), Colors.CodinuxSecondaryColor)
            }

            Section(Res.string.invoice_details) {
                InvoiceDetailsForm(details, isCompactScreen)
            }

            Section(Res.string.supplier) {
                InvoicePartyForm(supplier, true, isCompactScreen, settings.showAllSupplierFields) {
                    invoiceDataChanged(settings.copy(showAllSupplierFields = it))
                }
            }

            Section(Res.string.customer) {
                InvoicePartyForm(customer, false, isCompactScreen, settings.showAllCustomerFields) {
                    invoiceDataChanged(settings.copy(showAllCustomerFields = it))
                }
            }

            Section(Res.string.description_of_services) {
                DescriptionOfServicesForm(descriptionOfServices, isCompactScreen)
            }

            Section(Res.string.bank_details) {
                BankDetailsForm(bankDetails)
            }

            Section(Res.string.create) {
                CreateInvoiceForm(settings, details, supplier, customer, descriptionOfServices, bankDetails, isCompactScreen)
            }

            AvoidCutOffAtEndOfScreen()
        }
    }
}