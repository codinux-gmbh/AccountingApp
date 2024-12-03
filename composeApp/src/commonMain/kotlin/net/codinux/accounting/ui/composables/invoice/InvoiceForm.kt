package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import io.github.vinceglb.filekit.core.baseName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.invoice.model.CreateEInvoiceOptions
import net.codinux.accounting.domain.invoice.model.HistoricalInvoiceData
import net.codinux.accounting.domain.invoice.model.ServiceDateOptions
import net.codinux.accounting.resources.*
import net.codinux.accounting.platform.Platform
import net.codinux.accounting.platform.isDesktop
import net.codinux.accounting.ui.composables.forms.*
import net.codinux.accounting.ui.composables.forms.datetime.DatePicker
import net.codinux.accounting.ui.composables.forms.datetime.SelectMonth
import net.codinux.accounting.ui.composables.invoice.model.*
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.config.Style
import net.codinux.accounting.ui.extensions.*
import net.codinux.invoicing.model.*
import net.codinux.log.Log
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


    val details by remember(historicalData) { mutableStateOf(InvoiceDetailsViewModel(historicalData.lastCreatedInvoice?.details)) }

    val areInvoiceDetailsValid by details.isValid.collectAsState()

    val supplier by remember(historicalData) { mutableStateOf(PartyViewModel(historicalData.lastCreatedInvoice?.supplier)) }

    val isSupplierValid by supplier.isValid.collectAsState()

    val customer by remember(historicalData) { mutableStateOf(PartyViewModel(historicalData.lastCreatedInvoice?.customer)) }

    val isCustomerValid by customer.isValid.collectAsState()

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

    val areInvoiceItemsValid = combine(invoiceItems.map { it.isValid }) {
        it.all { it }
    }.collectAsState(false)

    val isValid by remember(areInvoiceDetailsValid, isSupplierValid, isCustomerValid, areInvoiceItemsValid) {
        // TODO: Date of delivery or performance of the service is also required
        derivedStateOf { areInvoiceDetailsValid and isSupplierValid and isCustomerValid and invoiceItems.isNotEmpty() and areInvoiceItemsValid.value }
    }


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

    fun nullable(value: StateFlow<String>): String? = value.value.takeUnless { it.isBlank() }

    fun createInvoice(): Invoice {
        val mappedBankDetails = if (bankDetails.accountNumber.value.isNotBlank()) BankDetails(bankDetails.accountNumber.value, nullable(bankDetails.bankCode), nullable(bankDetails.accountHolderName) ?: supplier.name.value, nullable(bankDetails.bankName))
        else null

        return Invoice(
            InvoiceDetails(details.invoiceNumber.value, details.invoiceDate.value),
            Party(supplier.name.value, supplier.address.value, null, supplier.postalCode.value, supplier.city.value, null, nullable(supplier.vatId), nullable(supplier.email), nullable(supplier.phone), bankDetails = mappedBankDetails),
            Party(customer.name.value, customer.address.value, null, customer.postalCode.value, customer.city.value, null, nullable(customer.vatId), nullable(customer.email), nullable(customer.phone)),
            invoiceItems.map { InvoiceItem(it.name.value, it.quantity.value!!, it.unit.value, it.unitPrice.value!!, it.vatRate.value!!, it.description.value) }
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
            InvoiceDetailsForm(details, isLargeDisplay)
        }

        Section(Res.string.supplier) {
            InvoicePartyForm(supplier)
        }

        Section(Res.string.customer) {
            InvoicePartyForm(customer)
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

                TextButton({ createEInvoice() }, enabled = isValid) {
                    Text(stringResource(Res.string.create), color = Colors.CodinuxSecondaryColor)
                }
            }

            generatedEInvoiceXml?.let { generatedEInvoiceXml ->
                Row(Modifier.padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
                    TextButton({ clipboardManager.setText(AnnotatedString(generatedEInvoiceXml)) }, contentPadding = PaddingValues(0.dp)) {
                        Text(stringResource(Res.string.copy), Modifier.width(95.dp), Colors.CodinuxSecondaryColor, textAlign = TextAlign.Center)
                    }

                    TextButton(onClick = { saveFileLauncher.launch(generatedEInvoiceXml.encodeToByteArray(), "invoice-${details.invoiceNumber.value}", "xml") }, contentPadding = PaddingValues(0.dp)) {
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