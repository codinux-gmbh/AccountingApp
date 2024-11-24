package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.codinux.accounting.resources.*
import net.codinux.accounting.resources.Res
import net.codinux.accounting.ui.composables.forms.OutlinedTextField
import net.codinux.accounting.ui.composables.forms.RoundedCornersCard
import net.codinux.accounting.ui.composables.forms.SectionHeader
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.config.Style
import net.codinux.accounting.ui.extensions.*
import net.codinux.invoicing.model.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.math.BigDecimal
import java.time.LocalDate


private val VerticalRowPadding = 2.dp

private val VerticalSectionPadding = 12.dp

private val PlaceholderTextColor = Colors.Zinc500

@Composable
fun InvoiceForm() {

    var invoiceDate = rememberSaveable { mutableStateOf(LocalDate.now().toString()) }

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

    var itemName = rememberSaveable { mutableStateOf("") }
    var itemQuantity = rememberSaveable { mutableStateOf("") }
    var itemUnit = rememberSaveable { mutableStateOf("") }
    var itemUnitPrice = rememberSaveable { mutableStateOf("") }
    var itemVatRate = rememberSaveable { mutableStateOf("") }


    var generatedEInvoiceXml by rememberSaveable { mutableStateOf<String?>(null) }

    val clipboardManager = LocalClipboardManager.current

    val coroutineScope = rememberCoroutineScope()


    fun nullable(value: MutableState<String>): String? = value.value.takeUnless { it.isBlank() }

    fun generateEInvoice() {
        coroutineScope.launch(Dispatchers.Default) {
            val bankDetails = if (iban.value.isNotBlank()) BankDetails(iban.value, nullable(bic), nullable(accountHolder) ?: issuerName.value, nullable(bankName))
            else null
            val invoice = Invoice(
                invoiceNumber.value, LocalDate.parse(invoiceDate.value),
                Party(issuerName.value, issuerStreet.value, issuerPostalCode.value, issuerCity.value, null, nullable(issuerVatId), nullable((issuerEmail)), bankDetails = bankDetails),
                Party(recipientName.value, recipientStreet.value, recipientPostalCode.value, recipientCity.value, null, null, nullable(recipientEmail)),
                listOf(InvoiceItem(itemName.value, BigDecimal(itemQuantity.value), itemUnit.value, BigDecimal(itemUnitPrice.value), BigDecimal(itemVatRate.value)))
            )

            // TODO: care for iOS display bug of long texts
            generatedEInvoiceXml = DI.invoiceService.createEInvoiceXml(invoice)
        }
    }


    Column(Modifier.fillMaxWidth().rememberVerticalScroll()) {
        Section(Res.string.invoice_details) {
            // TODO: use a date picker
            InvoiceTextField(invoiceDate, Res.string.invoice_date)

            InvoiceTextField(invoiceNumber, Res.string.invoice_number)
        }

        Section(Res.string.issuer) {
            PersonFields(issuerName, issuerStreet, issuerPostalCode, issuerCity, issuerEmail)

            InvoiceTextField(issuerVatId, Res.string.vat_id_or_tax_number)
        }

        Section(Res.string.recipient) {
            PersonFields(recipientName, recipientStreet, recipientPostalCode, recipientCity, recipientEmail)
        }

        Section(Res.string.description_of_services) {
            Text(stringResource(Res.string.service_date)) // TODO: add two DatePicker fields and add selection box for delivery date, service date and service period

            Text(stringResource(Res.string.delivered_goods_or_provided_services))

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(Res.string.name), fontWeight = FontWeight.Bold)

                    InvoiceTextField(itemName, Res.string.name)
                }

                Column(Modifier.padding(start = 4.dp).width(75.dp)) {
                    Text(stringResource(Res.string.quantity), fontWeight = FontWeight.Bold)

                    InvoiceTextField(itemQuantity, Res.string.quantity)
                }

                Column(Modifier.padding(start = 4.dp).width(75.dp)) {
                    Text(stringResource(Res.string.unit), fontWeight = FontWeight.Bold)

                    InvoiceTextField(itemUnit, Res.string.unit)
                }

                Column(Modifier.padding(start = 4.dp).width(75.dp)) {
                    Text(stringResource(Res.string.unit_price), fontWeight = FontWeight.Bold)

                    InvoiceTextField(itemUnitPrice, Res.string.unit_price)
                }

                Column(Modifier.padding(start = 4.dp).width(75.dp)) {
                    Text(stringResource(Res.string.vat_rate), fontWeight = FontWeight.Bold)

                    InvoiceTextField(itemVatRate, Res.string.vat_rate)
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
                        Text(stringResource(Res.string.copy), color = Colors.CodinuxSecondaryColor)
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