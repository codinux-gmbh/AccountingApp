package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.forms.Section
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.dialogs.ViewInvoiceDialog
import net.codinux.accounting.ui.extensions.parent
import net.codinux.invoicing.reader.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun SelectEInvoiceFileToDisplay() {

    var lastSelectedInvoiceFile by remember { mutableStateOf<PlatformFile?>(null) }

    var lastExtractedEInvoice by remember { mutableStateOf<FileEInvoiceExtractionResult?>(null) }

    val coroutineScope = rememberCoroutineScope()


    val openExistingInvoiceFileLauncher = rememberFilePickerLauncher(
        PickerType.File(listOf("pdf", "xml")), stringResource(Res.string.select_e_invoice_file), lastSelectedInvoiceFile?.parent) { selectedFile ->
        selectedFile?.let {
            lastSelectedInvoiceFile = it

            coroutineScope.launch(Dispatchers.IO) {
                lastExtractedEInvoice = DI.invoiceService.readEInvoice(it)
            }
        }
    }


    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(Modifier.widthIn(max = 600.dp)) {
            Section(Res.string.show_e_invoice_file) {
                Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { openExistingInvoiceFileLauncher.launch() }, Modifier.fillMaxWidth().height(70.dp)) {
                        Text(stringResource(Res.string.select_e_invoice_file), Modifier, Colors.CodinuxSecondaryColor, textAlign = TextAlign.Center, maxLines = 1)
                    }
                }
            }
        }
    }


    fun showReadXmlError(result: FileEInvoiceExtractionResult, xmlResult: ReadEInvoiceXmlResult) {
        val stringResource = when (xmlResult.type) {
            ReadEInvoiceXmlResultType.InvalidXml -> Res.string.error_message_file_is_not_a_valid_xml
            ReadEInvoiceXmlResultType.InvalidInvoiceData -> Res.string.error_message_xml_file_contains_invalid_invoice_data
            else -> Res.string.error_message_could_not_read_e_invoice
        }

//        DI.uiState.errorOccurred(ErroneousAction.ReadEInvoice, stringResource, xmlResult.readError, result.path)
        DI.uiState.errorOccurred(ErroneousAction.ReadEInvoice, stringResource, null, result.filename)
    }

    lastExtractedEInvoice?.let { result ->
        val invoice = result.invoice
        val pdfResult = result.pdf
        val xmlResult = result.xml

        if (invoice != null) {
            ViewInvoiceDialog(invoice) { lastExtractedEInvoice = null }
        } else if (pdfResult != null) {
            val stringResource = when (pdfResult.type) {
                PdfExtractionResultType.NotAPdf -> Res.string.error_message_file_is_not_a_pdf
                PdfExtractionResultType.NoAttachments -> Res.string.error_message_pdf_has_no_attachments
                PdfExtractionResultType.NoXmlAttachments -> Res.string.error_message_pdf_has_no_xml_attachments
                else -> null // should never come to here
            }
            if (stringResource != null) {
                DI.uiState.errorOccurred(ErroneousAction.ReadEInvoice, stringResource)
            }
        } else if (xmlResult != null) {
            showReadXmlError(result, xmlResult)
        }
    }

}