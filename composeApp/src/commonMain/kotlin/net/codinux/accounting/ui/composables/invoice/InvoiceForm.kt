package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.codinux.accounting.domain.invoice.model.ServiceDateOptions
import net.codinux.accounting.resources.*
import net.codinux.accounting.platform.Platform
import net.codinux.accounting.platform.isDesktop
import net.codinux.accounting.ui.composables.forms.*
import net.codinux.accounting.ui.composables.forms.datetime.DatePicker
import net.codinux.accounting.ui.composables.forms.datetime.SelectMonth
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.config.Style
import net.codinux.accounting.ui.extensions.*
import net.codinux.invoicing.model.*
import net.codinux.log.Log
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.math.BigDecimal
import java.time.LocalDate


private val VerticalRowPadding = 2.dp

private val VerticalSectionPadding = 12.dp

private val PlaceholderTextColor = Colors.Zinc500

@Composable
fun InvoiceForm() {

    var invoiceDate by rememberSaveable { mutableStateOf(LocalDate.now()) }

    var invoiceNumber = rememberSaveable { mutableStateOf("") }

    var issuerName = remember { mutableStateOf("") }
    var issuerStreet = rememberSaveable { mutableStateOf("") }
    var issuerPostalCode = rememberSaveable { mutableStateOf("") }
    var issuerCity = rememberSaveable { mutableStateOf("") }
    var issuerEmail = rememberSaveable { mutableStateOf("") }
    var issuerVatId = rememberSaveable { mutableStateOf("") }

    var recipientName = rememberSaveable { mutableStateOf("") }
    var recipientStreet = rememberSaveable { mutableStateOf("") }
    var recipientPostalCode = rememberSaveable { mutableStateOf("") }
    var recipientCity = rememberSaveable { mutableStateOf("") }
    var recipientEmail = rememberSaveable { mutableStateOf("") }

    var accountHolder = rememberSaveable { mutableStateOf("") }
    var bankName = rememberSaveable { mutableStateOf("") }
    var iban = rememberSaveable { mutableStateOf("") }
    var bic = rememberSaveable { mutableStateOf("") }

    val servicePeriodDefaultMonth = LocalDate.now().minusMonths(1)
    var selectedServiceDateOption by rememberSaveable { mutableStateOf(ServiceDateOptions.ServicePeriodMonth) }
    var serviceDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var servicePeriodMonth by rememberSaveable { mutableStateOf(servicePeriodDefaultMonth.month) }
    var servicePeriodStart by rememberSaveable { mutableStateOf(servicePeriodDefaultMonth.withDayOfMonth(1)) }
    var servicePeriodEnd by rememberSaveable { mutableStateOf(servicePeriodDefaultMonth.withDayOfMonth(servicePeriodDefaultMonth.lengthOfMonth())) }
    val invoiceItems = remember { mutableStateListOf(EditableInvoiceItem()) }


    var generatedEInvoiceXml by rememberSaveable { mutableStateOf<String?>(null) }

    val clipboardManager = LocalClipboardManager.current

    val fileLauncher = rememberFileSaverLauncher { }

    val isLargeDisplay = Platform.isDesktop

    val coroutineScope = rememberCoroutineScope()


    @Composable
    fun getLabel(option: ServiceDateOptions): String = when (option) {
        ServiceDateOptions.DeliveryDate -> stringResource(Res.string.delivery_date)
        ServiceDateOptions.ServiceDate -> stringResource(Res.string.service_date)
        ServiceDateOptions.ServicePeriodMonth -> stringResource(Res.string.service_period_month)
        ServiceDateOptions.ServicePeriodCustom -> stringResource(Res.string.service_period)
    }

    fun nullable(value: MutableState<String>): String? = value.value.takeUnless { it.isBlank() }

    fun generateEInvoice() {
        coroutineScope.launch(Dispatchers.Default) {
            try {
                val bankDetails = if (iban.value.isNotBlank()) BankDetails(iban.value, nullable(bic), nullable(accountHolder) ?: issuerName.value, nullable(bankName))
                else null
                val invoice = Invoice(
                    invoiceNumber.value, invoiceDate,
                    Party(issuerName.value, issuerStreet.value, issuerPostalCode.value, issuerCity.value, null, nullable(issuerVatId), nullable((issuerEmail)), bankDetails = bankDetails),
                    Party(recipientName.value, recipientStreet.value, recipientPostalCode.value, recipientCity.value, null, null, nullable(recipientEmail)),
                    // TODO: add check if values are really set and add error handling
                    invoiceItems.map { InvoiceItem(it.name, it.quantity!!, it.unit, it.unitPrice!!, it.vatRate!!, it.description) }
                )

                // TODO: care for iOS display bug of long texts
                generatedEInvoiceXml = DI.invoiceService.createEInvoiceXml(invoice)
            } catch (e: Throwable) {
                Log.error(e) { "Could not create eInvoice" }

                // TODO: show error message to user
            }
        }
    }


    Column(Modifier.fillMaxWidth().rememberVerticalScroll()) {
        Section(Res.string.invoice_details) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                DatePicker(stringResource(Res.string.invoice_date), invoiceDate, Modifier.width(if (isLargeDisplay) 125.dp else 120.dp).fillMaxHeight(), true) { invoiceDate = it }

                Spacer(Modifier.width(6.dp))

                InvoiceTextField(invoiceNumber, Res.string.invoice_number)
            }
        }

        Section(Res.string.issuer) {
            PersonFields(issuerName, issuerStreet, issuerPostalCode, issuerCity, issuerEmail)

            InvoiceTextField(issuerVatId, Res.string.vat_id_or_tax_number)
        }

        Section(Res.string.recipient) {
            PersonFields(recipientName, recipientStreet, recipientPostalCode, recipientCity, recipientEmail)
        }

        Section(Res.string.description_of_services) {
            Row(Modifier.fillMaxWidth().padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
                Select("", ServiceDateOptions.entries, selectedServiceDateOption, { selectedServiceDateOption = it }, { getLabel(it) }, Modifier.padding(end = 8.dp).width(if (isLargeDisplay) 210.dp else 185.dp))

                when (selectedServiceDateOption) {
                    ServiceDateOptions.DeliveryDate -> { DatePicker("", serviceDate) { serviceDate = it } }
                    ServiceDateOptions.ServiceDate -> { DatePicker("", serviceDate) { serviceDate = it } }
                    ServiceDateOptions.ServicePeriodMonth -> { SelectMonth(servicePeriodMonth) { servicePeriodMonth = it } }
                    ServiceDateOptions.ServicePeriodCustom -> {
                        DatePicker(stringResource(Res.string.service_period_start), servicePeriodStart, moveFocusOnToNextElementOnSelection = false) { servicePeriodStart = it }
                        Text("-", textAlign = TextAlign.Center, modifier = Modifier.width(18.dp))
                        DatePicker(stringResource(Res.string.service_period_end), servicePeriodEnd) { servicePeriodEnd = it }
                    }
                }
            }

            Column(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(top = 12.dp).height(30.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(Res.string.delivered_goods_or_provided_services), fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)

                    Spacer(Modifier.weight(1f))

                    TextButton({ invoiceItems.add(EditableInvoiceItem()) }, contentPadding = PaddingValues(0.dp)) {
                        Icon(Icons.Outlined.Add, "Add invoice item", Modifier.width(48.dp).fillMaxHeight(), Colors.CodinuxSecondaryColor)
                    }
                }

                invoiceItems.toList().forEach { item ->
                    InvoiceItemForm(item, PlaceholderTextColor)
                }
            }
        }

        Section(Res.string.bank_details) {
            InvoiceTextField(accountHolder, Res.string.account_holder_if_different)

            InvoiceTextField(bankName, Res.string.name_of_financial_institution)

            InvoiceTextField(iban, Res.string.iban)

            InvoiceTextField(bic, Res.string.bic)
        }

        Section(Res.string.create) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.width(1.dp).weight(1f))

                TextButton({ generateEInvoice() }) {
                    Text(stringResource(Res.string.create), color = Colors.CodinuxSecondaryColor)
                }
            }

            generatedEInvoiceXml?.let { generatedEInvoiceXml ->
                Row(Modifier.padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextButton({ clipboardManager.setText(AnnotatedString(generatedEInvoiceXml))}) {
                        Text(stringResource(Res.string.copy), Modifier.width(100.dp), Colors.CodinuxSecondaryColor, textAlign = TextAlign.Center)
                    }

                    TextButton(onClick = { fileLauncher.launch(generatedEInvoiceXml.encodeToByteArray(), "invoice-${invoiceNumber.value}", "xml") }) {
                        Text(stringResource(Res.string.save), Modifier.width(100.dp), Colors.CodinuxSecondaryColor, textAlign = TextAlign.Center)
                    }
                }

                Column(Modifier.rememberHorizontalScroll().background(Colors.Zinc100)) {
                    SelectionContainer(modifier = Modifier.fillMaxSize()) {
                        Text(generatedEInvoiceXml, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        Spacer(Modifier.padding(bottom = Style.MainScreenTabVerticalPadding))
    }
}

@Composable
private fun Section(titleResource: StringResource, content: @Composable () -> Unit) {
    RoundedCornersCard(Modifier.fillMaxWidth().padding(top = VerticalSectionPadding)) {
        Column(Modifier.padding(all = Style.FormCardPadding).padding(vertical = VerticalRowPadding)) {
            SectionHeader(stringResource(titleResource), false)

            content()
        }
    }
}

@Composable
private fun PersonFields(name: MutableState<String>, street: MutableState<String>, postalCode: MutableState<String>, city: MutableState<String>, email: MutableState<String>) {
    InvoiceTextField(name, Res.string.name)

    InvoiceTextField(street, Res.string.street)

    Row(Modifier.fillMaxWidth().padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
        InvoiceTextField(postalCode, Res.string.postal_code, Modifier.width(130.dp).height(56.dp).padding(end = 12.dp))

        InvoiceTextField(city, Res.string.city, Modifier.weight(1f))
    }

    InvoiceTextField(email, Res.string.email)
}

@Composable
private fun InvoiceTextField(value: MutableState<String>, labelResource: StringResource, modifier: Modifier = Modifier.fillMaxWidth().padding(top = VerticalRowPadding)) {
    OutlinedTextField(
        value.value,
        { value.value = it },
        modifier,
        label = { Text(stringResource(labelResource), color = PlaceholderTextColor, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        keyboardOptions = KeyboardOptions.ImeNext
    )
}