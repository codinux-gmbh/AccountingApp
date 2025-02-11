package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import net.codinux.accounting.domain.invoice.model.ViewInvoiceSettings
import net.codinux.accounting.resources.*
import net.codinux.accounting.platform.PlatformUiFunctions
import net.codinux.accounting.ui.composables.AvoidCutOffAtEndOfScreen
import net.codinux.accounting.ui.composables.HeaderText
import net.codinux.accounting.ui.composables.forms.*
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.config.Style
import net.codinux.accounting.ui.extensions.applyIf
import net.codinux.accounting.ui.extensions.rememberHorizontalScroll
import net.codinux.accounting.ui.extensions.verticalScroll
import net.codinux.invoicing.model.*
import net.codinux.invoicing.model.codes.Currency
import net.codinux.invoicing.reader.ReadEInvoicePdfResult
import org.jetbrains.compose.resources.stringResource


private val invoiceService = DI.invoiceService

private val formatUtil = DI.formatUtil

private val WarningSignColor = net.codinux.accounting.ui.extensions.Color("#FDCF0C")

@Composable
fun InvoiceView(mapInvoiceResult: MapInvoiceResult, readPdfResult: ReadEInvoicePdfResult? = null, invoiceXml: String? = null, enableVerticalScrolling: Boolean = true) {

    val invoice = mapInvoiceResult.invoice

    val xml = invoiceXml ?: readPdfResult?.attachmentExtractionResult?.invoiceXml

    val settings = DI.uiState.viewInvoiceSettings.collectAsState().value


    SelectionContainer(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().applyIf(enableVerticalScrolling) { it.verticalScroll() }) {
            if (mapInvoiceResult.invoiceDataErrors.isNotEmpty()) {
                Section(Res.string.invoice_contains_errors) {
                    Row(Modifier.fillMaxWidth().padding(top = Style.SectionTopPadding, bottom = 6.dp).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Warning, "warning sign", Modifier.size(24.dp), WarningSignColor)

                        Text(stringResource(Res.string.error_message_invoice_contains_errors), Modifier.padding(horizontal = 4.dp).weight(1f), textAlign = TextAlign.Center)

                        Icon(Icons.Outlined.Warning, "warning sign", Modifier.size(24.dp), WarningSignColor)
                    }

                    mapInvoiceResult.invoiceDataErrors.forEach { dataError ->
                        InvoiceDataErrorListItem(dataError)
                    }
                }
            }

            Section(Res.string.invoice_details) {
                HorizontalLabelledValue(Res.string.invoice_date, formatUtil.formatMediumDate(invoice.details.invoiceDate))

                HorizontalLabelledValue(Res.string.invoice_number, invoice.details.invoiceNumber)
            }

            Section(Res.string.supplier) {
                PersonFields(invoice.supplier)
            }

            Section(Res.string.customer) {
                PersonFields(invoice.customer)
            }

            Section(Res.string.description_of_services) {
                invoice.details.serviceDate?.asDeliveryDate()?.let { deliveryDate ->
                    HorizontalLabelledValue(Res.string.delivery_date, formatUtil.formatMediumDate(deliveryDate.deliveryDate))
                }
                invoice.details.serviceDate?.asServicePeriod()?.let { servicePeriod ->
                    val startDate = formatUtil.formatMediumDate(servicePeriod.startDate)
                    val endDate = formatUtil.formatMediumDate(servicePeriod.endDate)
                    HorizontalLabelledValue(Res.string.service_period, stringResource(Res.string.from_to, startDate, endDate))
                }

                Text(stringResource(Res.string.delivered_goods_or_provided_services), Modifier.padding(top = 8.dp), fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)

                invoice.items.forEachIndexed { index, item ->
                    InvoiceItemView(index, item, invoice.details.currency)
                }

                invoice.totals?.let { TotalAmountsView(invoice.details.currency, it, true) }
            }

            invoice.supplier.bankDetails?.let { BankDetailsView(it, invoice, settings) }

            if (xml != null) {
                Section(Res.string.invoice_file_details) {
                    InvoiceFileDetails(xml, readPdfResult, settings)
                }
            }

            AvoidCutOffAtEndOfScreen()
        }
    }

}


@Composable
private fun PersonFields(party: Party) {
    HorizontalLabelledValue(Res.string.name, party.name)

    HorizontalLabelledValue(Res.string.address, party.address)

    HorizontalLabelledValue(Res.string.city, "${party.postalCode} ${party.city} ${party.country.alpha2Code}") // TODO: translate; // TODO: this can't be valid that all countries have an alpha-2 code

    HorizontalLabelledValue(Res.string.email, party.email)
    party.phone?.let { HorizontalLabelledValue(Res.string.phone, party.phone) }
    party.fax?.let { HorizontalLabelledValue(Res.string.fax, party.fax) }
    party.contactName?.let { HorizontalLabelledValue(Res.string.contact_name, party.contactName) }

    HorizontalLabelledValue(Res.string.vat_id_or_tax_number, party.vatId)
}

@Composable
private fun InvoiceItemView(zeroBasedItemIndex: Int, item: InvoiceItem, currency: Currency) {
    Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("${zeroBasedItemIndex + 1}.", Modifier.padding(start = 4.dp), textAlign = TextAlign.End)

        Text(item.name, Modifier.padding(start = 4.dp).weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)

        Text(formatUtil.formatQuantity(item.quantity), Modifier.width(32.dp).padding(start = 4.dp), textAlign = TextAlign.End, maxLines = 1)
        Text(item.unit.symbol ?: item.unit.englishName, Modifier.width(32.dp).padding(start = 4.dp), maxLines = 1) // TODO: translate
        Text("à", Modifier.padding(start = 4.dp))
        Text(formatUtil.formatAmountOfMoney(item.unitPrice, currency, true), Modifier.width(64.dp).padding(start = 4.dp), maxLines = 1)
        // Text(",")
        Text(formatUtil.formatPercentage(item.vatRate), Modifier.width(36.dp).padding(start = 4.dp), textAlign = TextAlign.End, maxLines = 1)
    }
}

@Composable
private fun BankDetailsView(details: BankDetails, invoice: Invoice, settings: ViewInvoiceSettings) {
    val accountHolderName = details.accountHolderName

    val eqcQrCode by remember { mutableStateOf(invoiceService.generateEpcQrCode(details, invoice, accountHolderName ?: invoice.supplier.name)) }

    val showEpcQrCode = settings.showEpcQrCode

    val coroutineScope = rememberCoroutineScope()


    fun saveViewInvoiceSettings(settings: ViewInvoiceSettings) {
        coroutineScope.launch {
            invoiceService.saveViewInvoiceSettings(settings)
        }
    }


    Section(Res.string.bank_details) {
        HorizontalLabelledValue(Res.string.account_holder, accountHolderName)

        HorizontalLabelledValue(Res.string.name_of_financial_institution, details.financialInstitutionName ?: "")

        HorizontalLabelledValue(Res.string.iban, details.accountNumber)

        HorizontalLabelledValue(Res.string.bic, details.bankCode)

        eqcQrCode?.let { eqcQrCode ->
            Row(Modifier.fillMaxWidth().padding(top = Style.SectionTopPadding)) {
                BooleanOption(Res.string.show_epc_qr_code, showEpcQrCode) {
                    saveViewInvoiceSettings(settings.copy(showEpcQrCode = it))
                }
            }

            if (showEpcQrCode) {
                Row(Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 4.dp).heightIn(max = 250.dp), horizontalArrangement = Arrangement.Center) {
                    Image(PlatformUiFunctions.createImageBitmap(eqcQrCode), "QR Code with bank details. Scan for easy wire transfer")
                }
            }
        }
    }
}

@Composable
private fun InvoiceFileDetails(xml: String, readPdfResult: ReadEInvoicePdfResult?, settings: ViewInvoiceSettings) {

    val showInvoiceXml = settings.showInvoiceXml

    val showPdfDetails = settings.showPdfDetails

    val clipboardManager = LocalClipboardManager.current

    val coroutineScope = rememberCoroutineScope()


    fun saveViewInvoiceSettings(settings: ViewInvoiceSettings) {
        coroutineScope.launch {
            invoiceService.saveViewInvoiceSettings(settings)
        }
    }


    if (readPdfResult != null) {
        Row(Modifier.padding(top = Style.FormVerticalRowPadding).height(36.dp), verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f))

            BooleanOption(Res.string.show_pdf_details, showPdfDetails) {
                saveViewInvoiceSettings(settings.copy(showPdfDetails = it))
            }
        }

        if (showPdfDetails) {
            Column(Modifier.fillMaxWidth().padding(top = Style.FormVerticalRowPadding, bottom = 12.dp).rememberHorizontalScroll()) {
                HeaderText(stringResource(Res.string.file_attachments), fontSize = 15.sp)

                readPdfResult.attachmentExtractionResult.attachments.forEach { attachment ->
                    RoundedCornersCard(Modifier.padding(top = 12.dp)) {
                        Row(Modifier.padding(horizontal = 8.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Attachment, "PDF attachment")

                            Text(attachment.filename + (if (attachment.isProbablyEN16931InvoiceXml) " (${stringResource(Res.string.e_invoice)})" else ""), Modifier.padding(start = 4.dp))
                        }
                    }
                }
            }
        }
    }


    Row(Modifier.padding(top = Style.FormVerticalRowPadding).height(36.dp), verticalAlignment = Alignment.CenterVertically) {
        TextButton({ clipboardManager.setText(AnnotatedString(xml)) }) {
            Text(stringResource(Res.string.copy_xml), Modifier.width(130.dp), Colors.HighlightedTextColor)
        }

        Spacer(Modifier.weight(1f))

        BooleanOption(Res.string.show_xml, showInvoiceXml) {
            saveViewInvoiceSettings(settings.copy(showInvoiceXml = it))
        }
    }

    if (showInvoiceXml) {
        Column(Modifier.padding(top = Style.FormVerticalRowPadding).rememberHorizontalScroll().background(Colors.MainBackgroundColor)) {
            SelectionContainer(modifier = Modifier.fillMaxSize()) {
                Text(xml, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
private fun InvoiceDataErrorListItem(dataError: InvoiceDataError) {
    val fieldName = when (dataError.field) {
        InvoiceField.InvoiceDate -> Res.string.invoice_field_invoice_date
        InvoiceField.InvoiceNumber -> Res.string.invoice_field_invoice_number

        InvoiceField.Currency -> Res.string.invoice_field_currency

        InvoiceField.Supplier -> Res.string.invoice_field_supplier
        InvoiceField.SupplierCountry -> Res.string.invoice_field_supplier_country
        InvoiceField.Customer -> Res.string.invoice_field_customer
        InvoiceField.CustomerCountry -> Res.string.invoice_field_customer_country

        InvoiceField.Items -> Res.string.invoice_field_items
        InvoiceField.ItemName -> Res.string.invoice_field_item_name
        InvoiceField.ItemQuantity -> Res.string.invoice_field_item_quantity
        InvoiceField.ItemUnit -> Res.string.invoice_field_item_unit
        InvoiceField.ItemUnitPrice -> Res.string.invoice_field_item_unit_price

        InvoiceField.TotalAmount -> Res.string.invoice_field_total_amounts
        InvoiceField.LineTotalAmount -> Res.string.invoice_field_line_total_amount
        InvoiceField.TaxBasisTotalAmount -> Res.string.invoice_field_tax_basis_total_amount
        InvoiceField.GrandTotalAmount -> Res.string.invoice_field_grand_total_amount
        InvoiceField.DuePayableAmount -> Res.string.invoice_field_due_payable_amount
    }

    val errorMessage = when (dataError.errorType) {
        InvoiceDataErrorType.ValueNotSet -> Res.string.invoice_data_error_value_not_set
        InvoiceDataErrorType.ValueNotUpperCase -> Res.string.invoice_data_error_value_not_uppercase
        InvoiceDataErrorType.ValueIsInvalid -> Res.string.invoice_data_error_value_invalid
        InvoiceDataErrorType.CalculatedAmountsAreInvalid -> Res.string.invoice_data_error_calculated_amounts_are_invalid
    }

    HorizontalLabelledValue(fieldName, stringResource(errorMessage, dataError.erroneousValue ?: ""))
}