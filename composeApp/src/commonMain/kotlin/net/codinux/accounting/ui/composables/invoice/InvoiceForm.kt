package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import io.github.vinceglb.filekit.core.baseName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.invoice.model.CreateEInvoiceOptions
import net.codinux.accounting.domain.invoice.model.HistoricalInvoiceData
import net.codinux.accounting.domain.invoice.model.ServiceDateOptions
import net.codinux.accounting.resources.*
import net.codinux.accounting.platform.Platform
import net.codinux.accounting.platform.isDesktop
import net.codinux.accounting.ui.composables.forms.*
import net.codinux.accounting.ui.composables.forms.OutlinedTextField
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
import java.io.File
import java.time.LocalDate


private val VerticalRowPadding = Style.FormVerticalRowPadding

private val createEInvoiceOptions = CreateEInvoiceOptions.entries
    .filter { if (Platform.supportsCreatingPdfs == false && it == CreateEInvoiceOptions.CreateXmlAndPdf) false else true }

private val invoiceService = DI.invoiceService

@Composable
fun InvoiceForm() {

    val historicalData = DI.uiState.historicalInvoiceData.collectAsState().value


    var invoiceDate by remember { mutableStateOf(LocalDate.now()) }

    var invoiceNumber = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.details?.invoiceNumber ?: "") }

    var supplierName = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.supplier?.name ?: "") }
    var supplierAddress = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.supplier?.address ?: "") }
    var supplierPostalCode = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.supplier?.postalCode ?: "") }
    var supplierCity = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.supplier?.city ?: "") }
    var supplierEmail = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.supplier?.email ?: "") }
    var supplierPhone = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.supplier?.phone ?: "") }
    var supplierVatId = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.supplier?.vatId ?: "") }

    var customerName = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.customer?.name ?: "") }
    var customerAddress = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.customer?.address ?: "") }
    var customerPostalCode = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.customer?.postalCode ?: "") }
    var customerCity = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.customer?.city ?: "") }
    var customerEmail = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.customer?.email ?: "") }
    var customerPhone = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.customer?.phone ?: "") }
    var customerVatId = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.customer?.vatId ?: "") }

    var accountHolder = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.supplier?.bankDetails?.accountHolderName ?: "") }
    var bankName = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.supplier?.bankDetails?.financialInstitutionName ?: "") }
    var iban = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.supplier?.bankDetails?.accountNumber ?: "") }
    var bic = remember(historicalData) { mutableStateOf(historicalData.lastCreatedInvoice?.supplier?.bankDetails?.bankCode ?: "") }

    val servicePeriodDefaultMonth = LocalDate.now().minusMonths(1)
    var selectedServiceDateOption by remember(historicalData) { mutableStateOf(historicalData.selectedServiceDateOption) }
    var serviceDate by remember { mutableStateOf(LocalDate.now()) }
    var servicePeriodMonth by remember { mutableStateOf(servicePeriodDefaultMonth.month) }
    var servicePeriodStart by remember { mutableStateOf(servicePeriodDefaultMonth.withDayOfMonth(1)) }
    var servicePeriodEnd by remember { mutableStateOf(servicePeriodDefaultMonth.withDayOfMonth(servicePeriodDefaultMonth.lengthOfMonth())) }

    val invoiceItems: MutableList<EditableInvoiceItem> = remember(historicalData) { mutableStateListOf(
        *(historicalData.lastCreatedInvoice?.items?.map { it.toEditable() }?.toTypedArray() ?: arrayOf(EditableInvoiceItem()))
    ) }


    var selectedEInvoiceXmlFormat by remember(historicalData) { mutableStateOf(historicalData.selectedEInvoiceXmlFormat) }

    var selectedCreateEInvoiceOption by remember(historicalData) { mutableStateOf(historicalData.selectedCreateEInvoiceOption) }

    var generatedEInvoiceXml by remember { mutableStateOf<String?>(null) }

    var showGeneratedEInvoiceXml by remember(historicalData) { mutableStateOf(historicalData.showGeneratedEInvoiceXml) }


    val clipboardManager = LocalClipboardManager.current

    val coroutineScope = rememberCoroutineScope()

    var pdfToAttachXmlTo by remember { mutableStateOf<PlatformFile?>(null) }

    var createdPdfFile by remember { mutableStateOf<PlatformFile?>(null) }

    val openExistingPdfFileLauncher = rememberFilePickerLauncher(PickerType.File(listOf("pdf")), stringResource(Res.string.select_existing_pdf_to_attach_e_invoice_xml_to), pdfToAttachXmlTo?.parent) { selectedFile ->
        pdfToAttachXmlTo = selectedFile
    }

    val saveFileLauncher = rememberFileSaverLauncher { }

    val isLargeDisplay = Platform.isDesktop


    @Composable
    fun getLabel(option: ServiceDateOptions): String = when (option) {
        ServiceDateOptions.DeliveryDate -> stringResource(Res.string.delivery_date)
        ServiceDateOptions.ServiceDate -> stringResource(Res.string.service_date)
        ServiceDateOptions.ServicePeriodMonth -> stringResource(Res.string.service_period_month)
        ServiceDateOptions.ServicePeriodCustom -> stringResource(Res.string.service_period)
    }

    @Composable
    fun getLabel(format: EInvoiceXmlFormat): String = when (format) {
        EInvoiceXmlFormat.FacturX -> stringResource(Res.string.e_invoice_xml_format_factur_x)
        EInvoiceXmlFormat.XRechnung -> stringResource(Res.string.e_invoice_xml_format_x_rechnung)
    }

    @Composable
    fun getLabel(option: CreateEInvoiceOptions): String = when (option) {
        CreateEInvoiceOptions.XmlOnly -> stringResource(Res.string.create_only_xml)
        CreateEInvoiceOptions.CreateXmlAndAttachToExistingPdf -> stringResource(Res.string.create_xml_and_attach_to_existing_pdf)
        CreateEInvoiceOptions.CreateXmlAndPdf -> stringResource(Res.string.create_xml_and_pdf)
    }

    fun parentDirAndFilename(file: PlatformFile?): String? =
        file?.let { File(it.parentDirName, it.name).path }

    fun nullable(value: MutableState<String>): String? = value.value.takeUnless { it.isBlank() }

    fun createInvoice(): Invoice {
        val bankDetails = if (iban.value.isNotBlank()) BankDetails(iban.value, nullable(bic), nullable(accountHolder) ?: supplierName.value, nullable(bankName))
        else null

        return Invoice(
            InvoiceDetails(invoiceNumber.value, invoiceDate),
            Party(supplierName.value, supplierAddress.value, null, supplierPostalCode.value, supplierCity.value, null, nullable(supplierVatId), nullable(supplierEmail), nullable(supplierPhone), bankDetails = bankDetails),
            Party(customerName.value, customerAddress.value, null, customerPostalCode.value, customerCity.value, null, null, nullable(customerEmail), nullable(customerPhone)),
            // TODO: add check if values are really set and add error handling
            invoiceItems.map { InvoiceItem(it.name, it.quantity!!, it.unit, it.unitPrice!!, it.vatRate!!, it.description) }
        )
    }

    fun createEInvoice() {
        coroutineScope.launch(Dispatchers.Default) {
            try {
                val invoice = createInvoice()

                generatedEInvoiceXml = when (selectedCreateEInvoiceOption) {
                    CreateEInvoiceOptions.XmlOnly -> invoiceService.createEInvoiceXml(invoice, selectedEInvoiceXmlFormat)
                    CreateEInvoiceOptions.CreateXmlAndAttachToExistingPdf -> invoiceService.attachEInvoiceXmlToPdf(invoice, selectedEInvoiceXmlFormat, pdfToAttachXmlTo!!)
                    CreateEInvoiceOptions.CreateXmlAndPdf -> invoiceService.createEInvoicePdf(invoice, selectedEInvoiceXmlFormat).let { (xml, pdf) ->
                        createdPdfFile = pdf
                        coroutineScope.launch {
                            DI.fileHandler.openFileInDefaultViewer(pdf)
                        }
                        xml
                    }
                }

                invoiceService.saveHistoricalInvoiceData(HistoricalInvoiceData(invoice, selectedServiceDateOption, selectedEInvoiceXmlFormat, selectedCreateEInvoiceOption, showGeneratedEInvoiceXml))
            } catch (e: Throwable) {
                Log.error(e) { "Could not create or save eInvoice" }

                DI.uiState.errorOccurred(ErroneousAction.CreateInvoice, Res.string.error_message_could_not_create_invoice, e)
            }
        }
    }


    Column(Modifier.fillMaxWidth()) {
        Section(Res.string.invoice_details) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                DatePicker(stringResource(Res.string.invoice_date), invoiceDate, Modifier.width(if (isLargeDisplay) 125.dp else 120.dp).fillMaxHeight(), true) { invoiceDate = it }

                Spacer(Modifier.width(6.dp))

                InvoiceTextField(invoiceNumber, Res.string.invoice_number)
            }
        }

        Section(Res.string.supplier) {
            PersonFields(supplierName, supplierAddress, supplierPostalCode, supplierCity, supplierEmail, supplierPhone, supplierVatId)
        }

        Section(Res.string.customer) {
            PersonFields(customerName, customerAddress, customerPostalCode, customerCity, customerEmail, customerPhone, customerVatId)
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
                    InvoiceItemForm(item)
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
                Select("", createEInvoiceOptions, selectedCreateEInvoiceOption, { selectedCreateEInvoiceOption = it }, { getLabel(it) })
            }

            if (selectedCreateEInvoiceOption == CreateEInvoiceOptions.CreateXmlAndAttachToExistingPdf) {
                Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { openExistingPdfFileLauncher.launch() }, Modifier.fillMaxWidth()) {
                        Text(stringResource(Res.string.select_existing_pdf_to_attach_e_invoice_xml_to), Modifier, Colors.CodinuxSecondaryColor, textAlign = TextAlign.Center, maxLines = 1)
                    }
                }

                if (pdfToAttachXmlTo != null) {
                    Row(Modifier.fillMaxWidth().padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
                        Text(parentDirAndFilename(pdfToAttachXmlTo) ?: "", Modifier.padding(horizontal = 4.dp))
                    }
                }
            }

            Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Select(stringResource(Res.string.e_invoice_xml_format), EInvoiceXmlFormat.entries, selectedEInvoiceXmlFormat, { selectedEInvoiceXmlFormat = it }, { getLabel(it) }, Modifier.width(200.dp))

                Spacer(Modifier.width(1.dp).weight(1f))

                TextButton({ createEInvoice() }) {
                    Text(stringResource(Res.string.create), color = Colors.CodinuxSecondaryColor)
                }
            }

            generatedEInvoiceXml?.let { generatedEInvoiceXml ->
                Row(Modifier.padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
                    TextButton({ clipboardManager.setText(AnnotatedString(generatedEInvoiceXml)) }, contentPadding = PaddingValues(0.dp)) {
                        Text(stringResource(Res.string.copy), Modifier.width(95.dp), Colors.CodinuxSecondaryColor, textAlign = TextAlign.Center)
                    }

                    TextButton(onClick = { saveFileLauncher.launch(generatedEInvoiceXml.encodeToByteArray(), "invoice-${invoiceNumber.value}", "xml") }, contentPadding = PaddingValues(0.dp)) {
                        Text(stringResource(Res.string.save_xml), Modifier.width(120.dp), Colors.CodinuxSecondaryColor, textAlign = TextAlign.Center)
                    }

                    createdPdfFile?.let { createdPdfFile ->
                        TextButton(onClick = { saveFileLauncher.launch(null, createdPdfFile.baseName, "pdf", createdPdfFile.parent) }) {
                            Text(stringResource(Res.string.save_pdf), Modifier.width(120.dp), Colors.CodinuxSecondaryColor, textAlign = TextAlign.Center)
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    BooleanOption(Res.string.show_xml, showGeneratedEInvoiceXml) { showGeneratedEInvoiceXml = it }
                }

                if (showGeneratedEInvoiceXml) {
                    Column(Modifier.rememberHorizontalScroll().background(Colors.MainBackgroundColor)) {
                        SelectionContainer(modifier = Modifier.fillMaxSize()) {
                            Text(generatedEInvoiceXml, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.padding(bottom = Style.MainScreenTabVerticalPadding))
    }
}

@Composable
private fun PersonFields(name: MutableState<String>, address: MutableState<String>, postalCode: MutableState<String>, city: MutableState<String>, email: MutableState<String>, phone: MutableState<String>, vatId: MutableState<String>) {
    InvoiceTextField(name, Res.string.name)

    InvoiceTextField(address, Res.string.address)

    Row(Modifier.fillMaxWidth().padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
        InvoiceTextField(postalCode, Res.string.postal_code, Modifier.width(130.dp).height(56.dp).padding(end = 12.dp), KeyboardType.Ascii)

        InvoiceTextField(city, Res.string.city, Modifier.weight(1f))
    }

    InvoiceTextField(email, Res.string.email, keyboardType = KeyboardType.Email)

    InvoiceTextField(phone, Res.string.phone, keyboardType = KeyboardType.Phone)

    InvoiceTextField(vatId, Res.string.vat_id_or_tax_number, keyboardType = KeyboardType.Ascii)
}

@Composable
private fun InvoiceTextField(value: MutableState<String>, labelResource: StringResource, modifier: Modifier = Modifier.fillMaxWidth().padding(top = VerticalRowPadding), keyboardType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(
        value.value,
        { value.value = it },
        modifier,
        label = { Text(stringResource(labelResource), color = Colors.PlaceholderTextColor, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        keyboardOptions = KeyboardOptions.ImeNext.copy(keyboardType = keyboardType)
    )
}