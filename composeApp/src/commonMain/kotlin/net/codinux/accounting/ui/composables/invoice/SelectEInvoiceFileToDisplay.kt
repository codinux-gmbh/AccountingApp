package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import io.github.vinceglb.filekit.core.extension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.invoice.model.RecentlyViewedInvoice
import net.codinux.accounting.resources.*
import net.codinux.accounting.platform.IoOrDefault
import net.codinux.accounting.ui.composables.forms.DropdownMenuBox
import net.codinux.accounting.ui.composables.forms.Section
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.extensions.parent
import net.codinux.accounting.ui.extensions.parentDirAndFilename
import net.codinux.invoicing.reader.*
import net.codinux.kotlin.Platform
import net.codinux.kotlin.PlatformType
import net.codinux.log.Log
import org.jetbrains.compose.resources.stringResource

private val invoiceService = DI.invoiceService

private val uiState = DI.uiState

@Composable
fun SelectEInvoiceFileToDisplay(selectedInvoiceChanged: (ReadEInvoiceFileResult?, String?, ByteArray?) -> Unit) {

    var lastSelectedInvoiceFile by remember { mutableStateOf<PlatformFile?>(null) }

    var lastExtractedEInvoice by remember { mutableStateOf<ReadEInvoiceFileResult?>(null) }

    val recentlyViewedInvoices = uiState.recentlyViewedInvoices.collectAsState().value

    val settings = uiState.viewInvoiceSettings.collectAsState().value

    val initialDirectory = lastSelectedInvoiceFile?.parent ?: settings.lastSelectedInvoiceFile?.let { DI.fileHandler.fromPath(it).parent }

    val coroutineScope = rememberCoroutineScope()


    fun getDisplayText(recentlyViewedInvoice: RecentlyViewedInvoice): String {
        val file = DI.fileHandler.fromPath(recentlyViewedInvoice.path)
        val filename = file.parentDirAndFilename

        return "$filename${recentlyViewedInvoice.invoiceSummary?.let { " ($it)" } ?: ""}"
    }

    fun showSelectedFile(selectedFile: PlatformFile, addToRecentlyViewedInvoices: Boolean) {
        lastSelectedInvoiceFile = selectedFile
        settings.lastSelectedInvoiceFile = selectedFile.path

        coroutineScope.launch(Dispatchers.IoOrDefault) {
            try {
                lastExtractedEInvoice = invoiceService.readEInvoice(selectedFile)

                lastExtractedEInvoice?.let { selectedInvoice ->
                    val fileBytes = selectedFile.readBytes()
                    val fileExtension = selectedFile.extension.lowercase()
                    val xml = if (fileExtension == "xml") fileBytes.decodeToString() else null
                    val pdfBytes = if (fileExtension == "pdf") fileBytes else null
                    selectedInvoiceChanged(selectedInvoice, xml, pdfBytes)

                    if (addToRecentlyViewedInvoices) {
                        invoiceService.addRecentlyViewedInvoice(DI.fileHandler.getRestorablePath(selectedFile), selectedInvoice)
                    }
                }

                invoiceService.saveViewInvoiceSettings(settings)
            } catch (e: Throwable) {
                Log.error(e) { "Could not show selected file '$selectedFile'" }
                uiState.errorOccurred(ErroneousAction.ShowSelectedEInvoice, Res.string.error_message_could_not_read_selected_e_invoice, e, selectedFile.path)
            }
        }
    }

    fun showRecentlyViewedInvoice(recentlyViewedInvoice: RecentlyViewedInvoice) {
        try {
            val file = DI.fileHandler.fromPath(recentlyViewedInvoice.path)

            showSelectedFile(file, false)
        } catch (e: Throwable) {
            Log.error(e) { "Could not show recently viewed invoice '$recentlyViewedInvoice'" }
            uiState.errorOccurred(ErroneousAction.ShowSelectedEInvoice, Res.string.error_message_could_not_read_selected_e_invoice, e, recentlyViewedInvoice.path)
        }
    }

    val openExistingInvoiceFileLauncher = rememberFilePickerLauncher(PickerType.File(listOf("pdf", "xml")),
        stringResource(Res.string.select_e_invoice_file), initialDirectory) { selectedFile ->
        selectedFile?.let {
            showSelectedFile(selectedFile, true)
        }
    }


    Section(Res.string.show_e_invoice_file) {
        Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { openExistingInvoiceFileLauncher.launch() }, Modifier.fillMaxWidth().height(70.dp)) {
                Text(stringResource(Res.string.select_e_invoice_file), Modifier.fillMaxWidth(), Colors.HighlightedTextColor, textAlign = TextAlign.Center, maxLines = 1)
            }
        }

        if (recentlyViewedInvoices.isNotEmpty() && Platform.type != PlatformType.iOS) { // cannot restore external paths on iOS
            DropdownMenuBox(recentlyViewedInvoices, { showRecentlyViewedInvoice(it) }, { getDisplayText(it) }) {
                Row(Modifier.fillMaxWidth().height(48.dp).padding(top = 6.dp), Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    TextButton({ }) {
                        Text(stringResource(Res.string.recently_viewed_e_invoice_files), color = Colors.HighlightedTextColor, textAlign = TextAlign.Center, maxLines = 1)
                    }

                    Icon(Icons.Outlined.ArrowDropDown, "Show list with recently viewed invoice files", Modifier.width(48.dp), tint = Colors.FormValueTextColor)
                }
            }
        }
    }

    lastSelectedInvoiceFile?.let { selectedFile ->
        Row(Modifier.fillMaxWidth().padding(top = 36.dp).padding(horizontal = 18.dp).clickable { openExistingInvoiceFileLauncher.launch() }, Arrangement.Center, Alignment.CenterVertically) {
            Text(selectedFile.name, Modifier.fillMaxWidth(), fontSize = 17.sp, textAlign = TextAlign.Center, overflow = TextOverflow.Clip, maxLines = 1)
        }
    }


    fun showReadXmlError(result: ReadEInvoiceFileResult, xmlResult: ReadEInvoiceXmlResult) {
        val stringResource = when (xmlResult.type) {
            ReadEInvoiceXmlResultType.InvalidXml -> Res.string.error_message_file_is_not_a_valid_xml
            ReadEInvoiceXmlResultType.InvalidInvoiceData -> Res.string.error_message_xml_file_contains_invalid_invoice_data
            else -> Res.string.error_message_could_not_read_e_invoice
        }

        uiState.errorOccurred(ErroneousAction.ReadEInvoice, stringResource, xmlResult.readError, lastSelectedInvoiceFile?.path ?: result.filename)
    }


    LaunchedEffect(lastExtractedEInvoice) {
        lastExtractedEInvoice?.let { result ->
            val invoice = result.invoice
            val pdfResult = result.readPdfResult
            val xmlResult = result.readXmlResult

            if (invoice == null) {
                if (pdfResult != null) {
                    val stringResource = when (pdfResult.type) {
                        ReadEInvoicePdfResultType.TechnicalError -> Res.string.error_message_technical_error
                        ReadEInvoicePdfResultType.NotAPdf -> Res.string.error_message_file_is_not_a_pdf
                        ReadEInvoicePdfResultType.NoAttachments -> Res.string.error_message_pdf_has_no_attachments
                        ReadEInvoicePdfResultType.NoXmlAttachments -> Res.string.error_message_pdf_has_no_xml_attachments
                        ReadEInvoicePdfResultType.UnsupportedInvoiceFormat -> Res.string.error_message_invoice_format_is_not_supported
                        ReadEInvoicePdfResultType.InvalidXml -> Res.string.error_message_file_is_not_a_valid_xml
                        ReadEInvoicePdfResultType.InvalidInvoiceData -> Res.string.error_message_xml_file_contains_invalid_invoice_data
                        ReadEInvoicePdfResultType.Success -> null // should never come to here
                    }
                    if (stringResource != null) {
                        uiState.errorOccurred(ErroneousAction.ReadEInvoice, stringResource, pdfResult.readError)
                    }
                } else if (xmlResult != null) {
                    showReadXmlError(result, xmlResult)
                }
            }
        }
    }

}