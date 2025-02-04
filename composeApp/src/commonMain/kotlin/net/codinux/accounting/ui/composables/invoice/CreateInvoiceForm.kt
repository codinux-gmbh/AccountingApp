package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.compose.SaverResultLauncher
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.invoice.model.CreateEInvoiceOptions
import net.codinux.accounting.domain.invoice.model.CreateInvoiceSettings
import net.codinux.accounting.resources.*
import net.codinux.accounting.resources.copy
import net.codinux.accounting.platform.IoOrDefault
import net.codinux.accounting.ui.composables.forms.BooleanOption
import net.codinux.accounting.ui.composables.forms.Select
import net.codinux.accounting.ui.composables.invoice.model.*
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.config.Style
import net.codinux.accounting.ui.extensions.*
import net.codinux.accounting.ui.extensions.extension
import net.codinux.invoicing.model.*
import net.codinux.log.Log
import org.jetbrains.compose.resources.stringResource


private val VerticalRowPadding = Style.FormVerticalRowPadding

private val createEInvoiceOptions = CreateEInvoiceOptions.entries

private val invoiceService = DI.invoiceService

@Composable
fun CreateInvoiceForm(settings: CreateInvoiceSettings, details: InvoiceDetailsViewModel, supplier: PartyViewModel, customer: PartyViewModel, descriptionOfServices: DescriptionOfServicesViewModel, bankDetails: BankDetailsViewModel, isCompactScreen: Boolean) {

    val areInvoiceDetailsValid by details.isValid.collectAsState()

    val isSupplierValid by supplier.isValid.collectAsState()

    val isCustomerValid by customer.isValid.collectAsState()

    val invoiceItems by descriptionOfServices.items.collectAsState()

    val areInvoiceItemsValid = combine(invoiceItems.map { it.isValid }) {
        it.all { it }
    }.collectAsState(false)

    val isValid by remember(areInvoiceDetailsValid, isSupplierValid, isCustomerValid, areInvoiceItemsValid) {
        // TODO: Date of delivery or performance of the service is also required
        derivedStateOf { areInvoiceDetailsValid and isSupplierValid and isCustomerValid and invoiceItems.isNotEmpty() and areInvoiceItemsValid.value }
    }


    var selectedEInvoiceXmlFormat by remember(settings) { mutableStateOf(settings.selectedEInvoiceXmlFormat) }

    var selectedCreateEInvoiceOption by remember(settings) { mutableStateOf(settings.selectedCreateEInvoiceOption) }

    var isCreatingEInvoice by remember { mutableStateOf(false) }

    var generatedEInvoiceXml by remember { mutableStateOf<String?>(null) }

    var createdXmlFile by remember { mutableStateOf<PlatformFile?>(null) }

    var showGeneratedEInvoiceXml by remember(settings) { mutableStateOf(settings.showGeneratedEInvoiceXml) }


    val clipboardManager = LocalClipboardManager.current

    val coroutineScope = rememberCoroutineScope()

    var pdfToAttachXmlTo by remember { mutableStateOf<PlatformFile?>(null) }

    var createdPdfFile by remember { mutableStateOf<PlatformFile?>(null) }
    // PlatformFile.readBytes() is a suspend function, so we cannot call it on behalf in non-suspend function when 'Save PDF' is clicked
    var createdPdfBytes by remember { mutableStateOf<ByteArray?>(null) } // -> do it right after creating PDF and store PDF bytes in createdPdfBytes

    val openExistingPdfFileLauncher = rememberFilePickerLauncher(PickerType.File(listOf("pdf")), stringResource(Res.string.select_existing_pdf_to_attach_e_invoice_xml_to), pdfToAttachXmlTo?.parent ?: settings.lastOpenFileDirectory) { selectedFile ->
        if (selectedFile != null) {
            pdfToAttachXmlTo = selectedFile
            coroutineScope.launch {
                invoiceService.saveCreateInvoiceSettings(settings.copy(lastOpenFileDirectory = selectedFile.parent))
            }
        }
    }

    val saveFileLauncher = rememberFileSaverLauncher { file ->
        if (file != null) {
            coroutineScope.launch {
                if (file.extension.lowercase() == "xml") {
                    invoiceService.saveCreateInvoiceSettings(settings.copy(lastXmlSaveDirectory = file.parent))
                } else if (file.extension.lowercase() == "pdf") {
                    invoiceService.saveCreateInvoiceSettings(settings.copy(lastPdfSaveDirectory = file.parent))
                }
            }
        }
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
        file?.parentDirAndFilename

    fun nullable(value: StateFlow<String>): String? = value.value.takeUnless { it.isBlank() }

    fun createInvoice(): Invoice {
        val mappedBankDetails = if (bankDetails.accountNumber.value.isNotBlank()) BankDetails(bankDetails.accountNumber.value, nullable(bankDetails.bankCode), nullable(bankDetails.accountHolderName) ?: supplier.name.value, nullable(bankDetails.bankName))
        else null

        return Invoice(
            InvoiceDetails(details.invoiceNumber.value, details.invoiceDate.value, descriptionOfServices.currency.value, descriptionOfServices.serviceDate.value),
            Party(supplier.name.value, supplier.address.value, supplier.additionalAddressLine.value, supplier.postalCode.value, supplier.city.value, supplier.country.value, nullable(supplier.vatId), nullable(supplier.email), nullable(supplier.phone), bankDetails = mappedBankDetails),
            Party(customer.name.value, customer.address.value, customer.additionalAddressLine.value, customer.postalCode.value, customer.city.value, customer.country.value, nullable(customer.vatId), nullable(customer.email), nullable(customer.phone)),
            descriptionOfServices.items.value.map { it.toInvoiceItem() }
        )
    }

    suspend fun saveCreateInvoiceSettings(createInvoice: Invoice) {
        val newSettings = CreateInvoiceSettings(createInvoice, settings.showAllSupplierFields, settings.showAllCustomerFields,
            descriptionOfServices.serviceDateOption.value, selectedEInvoiceXmlFormat, selectedCreateEInvoiceOption, showGeneratedEInvoiceXml,
            settings.lastXmlSaveDirectory, settings.lastPdfSaveDirectory, settings.lastOpenFileDirectory)

        invoiceService.saveCreateInvoiceSettings(newSettings)
    }

    fun createEInvoice() {
        isCreatingEInvoice = true

        coroutineScope.launch(Dispatchers.IoOrDefault) {
            try {
                val invoice = createInvoice()

                val xmlToFile = when (selectedCreateEInvoiceOption) {
                    CreateEInvoiceOptions.XmlOnly -> invoiceService.createEInvoiceXml(invoice, selectedEInvoiceXmlFormat)
                    CreateEInvoiceOptions.CreateXmlAndAttachToExistingPdf -> invoiceService.attachEInvoiceXmlToPdf(invoice, selectedEInvoiceXmlFormat, pdfToAttachXmlTo!!)
                    CreateEInvoiceOptions.CreateXmlAndPdf -> invoiceService.createEInvoicePdf(invoice, selectedEInvoiceXmlFormat, { generatedEInvoiceXml = it })?.let { (xml, xmlFile, pdfBytes, pdf) ->
                        createdPdfFile = pdf
                        createdPdfBytes = pdfBytes

                        if (pdf != null) {
                            coroutineScope.launch(Dispatchers.IoOrDefault) {
                                DI.fileHandler.openFileInDefaultViewer(pdf, "application/pdf")
                            }
                        }
                        xml to xmlFile
                    }
                }

                generatedEInvoiceXml = xmlToFile?.first
                createdXmlFile = xmlToFile?.second

                isCreatingEInvoice = false

                saveCreateInvoiceSettings(invoice)
            } catch (e: Throwable) {
                Log.error(e) { "Could not create or save eInvoice" }

                DI.uiState.errorOccurred(ErroneousAction.CreateInvoice, Res.string.error_message_could_not_create_invoice, e)
            }
        }
    }


    if (isValid == false) {
        Row(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 6.dp).padding(horizontal = 12.dp), Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(Res.string.validation_message_create_invoice_not_all_required_fields_have_been_filled_out), color = MaterialTheme.colors.error, textAlign = TextAlign.Center)
        }
    }

    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Select(null, createEInvoiceOptions, selectedCreateEInvoiceOption, { selectedCreateEInvoiceOption = it }, { getLabel(it) })
    }

    if (selectedCreateEInvoiceOption == CreateEInvoiceOptions.CreateXmlAndAttachToExistingPdf) {
        Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { openExistingPdfFileLauncher.launch() }, Modifier.fillMaxWidth()) {
                Text(stringResource(Res.string.select_existing_pdf_to_attach_e_invoice_xml_to), Modifier, Colors.HighlightedTextColor, textAlign = TextAlign.Center, maxLines = 1)
            }
        }

        if (pdfToAttachXmlTo != null) {
            Row(Modifier.fillMaxWidth().padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
                Text(parentDirAndFilename(pdfToAttachXmlTo) ?: "", Modifier.padding(horizontal = 4.dp))
            }
        }
    }

    Row(Modifier.fillMaxWidth().padding(top = Style.SectionTopPadding), verticalAlignment = Alignment.CenterVertically) {
        Select(Res.string.e_invoice_xml_format, EInvoiceXmlFormat.entries, selectedEInvoiceXmlFormat, { selectedEInvoiceXmlFormat = it }, { getLabel(it) }, Modifier.width(200.dp))

        Spacer(Modifier.width(1.dp).weight(1f))

        if (isCreatingEInvoice) {
            CircularProgressIndicator(Modifier.padding(end = 12.dp).size(36.dp), color = Colors.HighlightedTextColor)
        }

        TextButton({ createEInvoice() }, contentPadding = PaddingValues(0.dp), enabled = isValid) {
            Text(stringResource(Res.string.create), Modifier.applyIf(isCreatingEInvoice == false) { it.width(150.dp) },
                color = if (isValid) Colors.HighlightedTextColor else Colors.HighlightedTextColorDisabled,
                fontWeight = if (isValid) FontWeight.SemiBold else FontWeight.Normal, textAlign = TextAlign.End)
        }
    }

    generatedEInvoiceXml?.let { generatedEInvoiceXml ->
        if (isCompactScreen && createdPdfFile != null) { // two lines on mobile
            Row(Modifier.fillMaxWidth().padding(top = Style.SectionTopPadding).height(36.dp), verticalAlignment = Alignment.CenterVertically) {
                SaveButtons(saveFileLauncher, generatedEInvoiceXml, createdXmlFile, createdPdfFile, createdPdfBytes, details, settings, 130.dp)
            }

            Row(Modifier.padding(top = VerticalRowPadding).height(36.dp), verticalAlignment = Alignment.CenterVertically) {
                TextButton({ clipboardManager.setText(AnnotatedString(generatedEInvoiceXml)) }) {
                    Text(stringResource(Res.string.copy), Modifier.width(130.dp), Colors.HighlightedTextColor)
                }

                Spacer(Modifier.weight(1f))

                BooleanOption(Res.string.show_xml, showGeneratedEInvoiceXml) { showGeneratedEInvoiceXml = it }
            }
        } else { // one line on Desktops
            Row(Modifier.padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
                TextButton({ clipboardManager.setText(AnnotatedString(generatedEInvoiceXml)) }, contentPadding = PaddingValues(0.dp)) {
                    Text(stringResource(Res.string.copy), Modifier.width(95.dp), Colors.HighlightedTextColor, textAlign = TextAlign.Center)
                }

                SaveButtons(saveFileLauncher, generatedEInvoiceXml, createdXmlFile, createdPdfFile, createdPdfBytes, details, settings, 120.dp)

                Spacer(Modifier.weight(1f))

                BooleanOption(Res.string.show_xml, showGeneratedEInvoiceXml) { showGeneratedEInvoiceXml = it }
            }
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

@Composable
private fun SaveButtons(
    saveFileLauncher: SaverResultLauncher,
    generatedEInvoiceXml: String,
    createdXmlFile: PlatformFile?,
    createdPdfFile: PlatformFile?,
    createdPdfBytes: ByteArray?,
    details: InvoiceDetailsViewModel,
    settings: CreateInvoiceSettings,
    buttonWidth: Dp
) {
    val invoiceFilename = createdPdfFile?.baseName.takeUnless { it.isNullOrBlank() } ?: createdXmlFile?.baseName.takeUnless { it.isNullOrBlank() }
                            ?: "invoice-${details.invoiceNumber.value}" // TODO: use same format as Invoice.shortDescription

    TextButton(onClick = { saveFileLauncher.launch(generatedEInvoiceXml.encodeToByteArray(), invoiceFilename, "xml", settings.lastXmlSaveDirectory) }, contentPadding = PaddingValues(0.dp)) {
        Text(stringResource(Res.string.save_xml), Modifier.width(buttonWidth), Colors.HighlightedTextColor, textAlign = TextAlign.Center)
    }

    createdPdfFile?.let {
        TextButton(onClick = { saveFileLauncher.launch(createdPdfBytes, invoiceFilename, "pdf", settings.lastPdfSaveDirectory) }) {
            Text(stringResource(Res.string.save_pdf), Modifier.width(buttonWidth), Colors.HighlightedTextColor, textAlign = TextAlign.Center)
        }
    }
}