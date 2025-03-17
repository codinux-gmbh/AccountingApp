package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import io.github.vinceglb.filekit.compose.SaverResultLauncher
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
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
import net.codinux.i18n.Language
import net.codinux.i18n.LanguageTag
import net.codinux.invoicing.format.EInvoiceFormat
import net.codinux.invoicing.model.*
import net.codinux.log.Log
import org.jetbrains.compose.resources.stringResource


private val VerticalRowPadding = Style.FormVerticalRowPadding

private val createEInvoiceOptions = CreateEInvoiceOptions.entries

private val invoiceService = DI.invoiceService

private val selectableEInvoiceFormats = listOf(EInvoiceFormat.FacturX, EInvoiceFormat.XRechnung)

private val createButtonWidth = 150.dp

private val createButtonPaddingStart = 6.dp

@Composable
fun CreateInvoiceForm(settings: CreateInvoiceSettings, details: InvoiceDetailsViewModel, supplier: PartyViewModel, customer: PartyViewModel, descriptionOfServices: DescriptionOfServicesViewModel, bankDetails: BankDetailsViewModel, settingsViewModel: CreateInvoiceSettingsViewModel, isCompactScreen: Boolean) {

    val templateSettings = DI.uiState.invoicePdfTemplateSettings.collectAsState().value

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


    var selectedEInvoiceFormat by remember(settings) { mutableStateOf(settings.selectedEInvoiceFormat) }

    var selectedCreateEInvoiceOption by remember(settings) { mutableStateOf(settings.selectedCreateEInvoiceOption) }

    val pdfTemplateViewModel by remember(templateSettings, settings) { mutableStateOf(PdfTemplateViewModel(templateSettings, settings)) }

    var isCreatingEInvoice by remember { mutableStateOf(false) }

    var createdInvoice by remember { mutableStateOf<Invoice?>(null) }

    var generatedEInvoiceXml by remember { mutableStateOf<String?>(null) }

    var createdXmlFile by remember { mutableStateOf<PlatformFile?>(null) }

    var showGeneratedEInvoiceXml by remember(settings) { mutableStateOf(settings.showGeneratedEInvoiceXml) }

    val screenWidthDp = DI.uiState.screenSize.collectAsState().value.widthDp


    val clipboardManager = LocalClipboardManager.current

    val coroutineScope = rememberCoroutineScope()

    var pdfToAttachXmlTo by remember { mutableStateOf<PlatformFile?>(null) }

    var createdPdfFile by remember { mutableStateOf<PlatformFile?>(null) }
    // PlatformFile.readBytes() is a suspend function, so we cannot call it on behalf in non-suspend function when 'Save PDF' is clicked
    var createdPdfBytes by remember { mutableStateOf<ByteArray?>(null) } // -> do it right after creating PDF and store PDF bytes in createdPdfBytes

    val openExistingPdfFileLauncher = rememberFilePickerLauncher(PickerType.File(listOf("pdf")), stringResource(Res.string.select_existing_pdf_to_attach_e_invoice_xml_to), pdfToAttachXmlTo?.parent ?: settings.lastOpenPdfDirectory) { selectedFile ->
        if (selectedFile != null) {
            pdfToAttachXmlTo = selectedFile
            settingsViewModel.lastOpenPdfDirectoryChanged(selectedFile.parent)
        }
    }

    val saveFileLauncher = rememberFileSaverLauncher { file ->
        if (file != null) {
            coroutineScope.launch {
                if (file.extension.lowercase() == "xml") {
                    settingsViewModel.lastXmlSaveDirectoryChanged(file.parent)
                } else if (file.extension.lowercase() == "pdf") {
                    settingsViewModel.lastPdfSaveDirectoryChanged(file.parent)
                }
            }
        }
    }


    @Composable
    fun getLabel(format: EInvoiceFormat): String = when (format) {
        EInvoiceFormat.FacturX, EInvoiceFormat.Zugferd -> stringResource(Res.string.e_invoice_xml_format_factur_x)
        EInvoiceFormat.XRechnung -> stringResource(Res.string.e_invoice_xml_format_x_rechnung)
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
        val mappedBankDetails = if (bankDetails.accountNumber.value.isNotBlank()) BankDetails(bankDetails.accountNumber.value, nullable(bankDetails.bankCode), nullable(bankDetails.accountHolderName), nullable(bankDetails.bankName))
        else null

        return Invoice(
            InvoiceDetails(details.invoiceNumber.value, details.invoiceDate.value, descriptionOfServices.currency.value, descriptionOfServices.serviceDate.value),
            Party(supplier.name.value, supplier.address.value, supplier.additionalAddressLine.value, supplier.postalCode.value, supplier.city.value, supplier.country.value, nullable(supplier.vatId), nullable(supplier.email), nullable(supplier.phone), bankDetails = mappedBankDetails),
            Party(customer.name.value, customer.address.value, customer.additionalAddressLine.value, customer.postalCode.value, customer.city.value, customer.country.value, nullable(customer.vatId), nullable(customer.email), nullable(customer.phone)),
            descriptionOfServices.items.value.map { it.toInvoiceItem() }
        )
    }

    suspend fun saveCreateInvoiceSettings(createInvoice: Invoice) {
        val newSettings = CreateInvoiceSettings(createInvoice, settingsViewModel.showAllSupplierFields.value,
            settingsViewModel.showAllCustomerFields.value, settingsViewModel.showAllBankDetailsFields.value,
            false,
            descriptionOfServices.serviceDateOption.value, selectedEInvoiceFormat, selectedCreateEInvoiceOption,
            showGeneratedEInvoiceXml,
            settingsViewModel.lastXmlSaveDirectory.value, settingsViewModel.lastPdfSaveDirectory.value,
            settingsViewModel.lastOpenPdfDirectory.value, pdfTemplateViewModel.lastOpenLogoDirectory.value)

        invoiceService.saveCreateInvoiceSettings(newSettings)
    }

    suspend fun saveInvoicePdfTemplateSettings() {
        invoiceService.saveInvoicePdfTemplateSettings(pdfTemplateViewModel.toTemplateSettings())
    }

    fun createEInvoice() {
        isCreatingEInvoice = true

        coroutineScope.launch(Dispatchers.IoOrDefault) {
            try {
                val invoice = createInvoice()

                val xmlToFile = when (selectedCreateEInvoiceOption) {
                    CreateEInvoiceOptions.XmlOnly -> invoiceService.createEInvoiceXml(invoice, selectedEInvoiceFormat)
                    CreateEInvoiceOptions.CreateXmlAndAttachToExistingPdf -> invoiceService.attachEInvoiceXmlToPdf(invoice, selectedEInvoiceFormat, pdfToAttachXmlTo!!) { createdInvoice = invoice; generatedEInvoiceXml = it }
                    CreateEInvoiceOptions.CreateXmlAndPdf -> invoiceService.createEInvoicePdf(invoice, selectedEInvoiceFormat, pdfTemplateViewModel.toTemplateSettings(),
                        { createdInvoice = invoice; generatedEInvoiceXml = it })?.let { (xml, xmlFile, pdf, pdfFile) ->
                        if (pdfFile != null && pdf != null && pdf.bytes.isNotEmpty()) {
                            createdPdfFile = pdfFile
                            createdPdfBytes = pdf.bytes

                            coroutineScope.launch(Dispatchers.IoOrDefault) {
                                DI.fileHandler.openFileInDefaultViewer(pdfFile, "application/pdf")
                            }
                        }
                        xml to xmlFile
                    }
                }

                if (xmlToFile?.first != null) {
                    createdInvoice = invoice
                    generatedEInvoiceXml = xmlToFile.first
                    createdXmlFile = xmlToFile.second
                }

                isCreatingEInvoice = false

                saveCreateInvoiceSettings(invoice)
                saveInvoicePdfTemplateSettings()
            } catch (e: Throwable) {
                invoiceService.showCouldNotCreateInvoiceError(e)
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

    if (selectedCreateEInvoiceOption == CreateEInvoiceOptions.CreateXmlAndPdf) {
        InvoicePdfSettingsForm(pdfTemplateViewModel, isCompactScreen)
    }

    Row(Modifier.fillMaxWidth().padding(top = Style.SectionTopPadding), verticalAlignment = Alignment.CenterVertically) {
        val remainingScreenWidth = screenWidthDp - createButtonWidth - createButtonPaddingStart

        val dropDownWidth = if (remainingScreenWidth >= 310.dp) null
                    else if (LanguageTag.current.language == Language.German && remainingScreenWidth <= 275.dp) 275.dp
                    else 310.dp

        Select(Res.string.e_invoice_xml_format, selectableEInvoiceFormats, selectedEInvoiceFormat,
            { selectedEInvoiceFormat = it }, { getLabel(it) }, Modifier.let { if (remainingScreenWidth >= 300.dp) it.widthIn(250.dp, 300.dp) else it.width(remainingScreenWidth) },
            dropDownWidth = dropDownWidth)

        Spacer(Modifier.width(createButtonPaddingStart).weight(1f))

        if (isCreatingEInvoice) {
            CircularProgressIndicator(Modifier.padding(start = 6.dp, end = 12.dp).size(36.dp), color = Colors.HighlightedTextColor)
        }

        TextButton({ createEInvoice() }, contentPadding = PaddingValues(0.dp), enabled = isValid) {
            Text(stringResource(Res.string.create), Modifier.applyIf(isCreatingEInvoice == false) { it.width(createButtonWidth) },
                color = if (isValid) Colors.HighlightedTextColor else Colors.HighlightedTextColorDisabled,
                fontWeight = if (isValid) FontWeight.SemiBold else FontWeight.Normal, textAlign = TextAlign.End)
        }
    }

    generatedEInvoiceXml?.let { invoiceXml ->
        Row(Modifier.padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
            TextButton({ clipboardManager.setText(AnnotatedString(invoiceXml)) }, contentPadding = PaddingValues(0.dp)) {
                Text(stringResource(Res.string.copy), Modifier.width(95.dp), Colors.HighlightedTextColor, textAlign = TextAlign.Center)
            }

            SaveButtons(saveFileLauncher, createdInvoice!!, invoiceXml, createdPdfBytes, settingsViewModel, if (isCompactScreen) 120.dp else 130.dp)

            Spacer(Modifier.weight(1f))

            BooleanOption(Res.string.show_xml, showGeneratedEInvoiceXml) { showGeneratedEInvoiceXml = it }
        }

        if (showGeneratedEInvoiceXml) {
            Column(Modifier.rememberHorizontalScroll().background(Colors.MainBackgroundColor)) {
                SelectionContainer(modifier = Modifier.fillMaxSize()) {
                    Text(invoiceXml, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }

}

@Composable
private fun SaveButtons(
    saveFileLauncher: SaverResultLauncher,
    invoice: Invoice,
    generatedEInvoiceXml: String,
    createdPdfBytes: ByteArray?,
    settingsViewModel: CreateInvoiceSettingsViewModel,
    buttonWidth: Dp
) {
    val invoiceFilename = invoice.shortDescription

    val lastXmlSaveDirectory = settingsViewModel.lastXmlSaveDirectory.collectAsState().value
    val lastPdfSaveDirectory = settingsViewModel.lastPdfSaveDirectory.collectAsState().value

    var showMenu by remember { mutableStateOf(false) }


    if (createdPdfBytes == null) { // only created XML
        TextButton(onClick = { saveFileLauncher.launch(generatedEInvoiceXml.encodeToByteArray(), invoiceFilename, "xml", lastXmlSaveDirectory) }, contentPadding = PaddingValues(0.dp)) {
            Text(stringResource(Res.string.save_xml), Modifier.width(buttonWidth), Colors.HighlightedTextColor, textAlign = TextAlign.Center)
        }
    } else {
        Box(modifier = Modifier.width(buttonWidth)) {
            TextButton(onClick = { showMenu = true }) {
                Row(Modifier.width(buttonWidth), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(Res.string.save), Modifier.weight(1f), Colors.HighlightedTextColor, textAlign = TextAlign.Center)
                    Icon(Icons.Outlined.ArrowDropDown, "Open menu to save PDF or XML file", Modifier.padding(start = 4.dp))
                }
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                // don't get it why on compact screens (Android) we have to use 0.dp for perfect align whilst on larger screen we have to set a negative offset and it still doesn't perfectly align
                //offset = DpOffset(x = if (isCompactScreen) 0.dp else -1 * (buttonWidth + additionalMenuPadding), y = 0.dp), // Offset to align to IconButton's end
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                DropdownMenuItem(
                    onClick = { showMenu = false; saveFileLauncher.launch(createdPdfBytes, invoiceFilename, "pdf", lastPdfSaveDirectory) },
                    content = { Text(stringResource(Res.string.save_pdf), color = Colors.HighlightedTextColor, textAlign = TextAlign.Center) }
                )
                DropdownMenuItem(
                    onClick = { showMenu = false; saveFileLauncher.launch(generatedEInvoiceXml.encodeToByteArray(), invoiceFilename, "xml", lastXmlSaveDirectory) },
                    content = { Text(stringResource(Res.string.save_xml), color = Colors.HighlightedTextColor, textAlign = TextAlign.Center) }
                )
            }
        }
    }

}