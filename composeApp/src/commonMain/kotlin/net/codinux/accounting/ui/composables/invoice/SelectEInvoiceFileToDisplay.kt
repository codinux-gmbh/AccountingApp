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
import net.codinux.accounting.resources.Res
import net.codinux.accounting.resources.select_e_invoice_file
import net.codinux.accounting.resources.show_e_invoice_file
import net.codinux.accounting.ui.composables.forms.Section
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.dialogs.ViewInvoiceDialog
import net.codinux.accounting.ui.extensions.parent
import net.codinux.invoicing.pdf.PdfAttachmentExtractionResultType
import net.codinux.invoicing.reader.FileEInvoiceExtractionResult
import org.jetbrains.compose.resources.stringResource

@Composable
fun SelectEInvoiceFileToDisplay() {

    var lastSelectedInvoiceFile by remember { mutableStateOf<PlatformFile?>(null) }

    var lastExtractedEInvoice by remember { mutableStateOf<FileEInvoiceExtractionResult?>(null) }

    val openExistingInvoiceFileLauncher = rememberFilePickerLauncher(
        PickerType.File(listOf("pdf", "xml")), stringResource(Res.string.select_e_invoice_file), lastSelectedInvoiceFile?.parent) { selectedFile ->
        selectedFile?.let {
            lastSelectedInvoiceFile = it

            lastExtractedEInvoice = DI.invoiceService.readEInvoice(it)
        }
    }


    Section(Res.string.show_e_invoice_file) {
        Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { openExistingInvoiceFileLauncher.launch() }, Modifier.fillMaxWidth().height(70.dp)) {
                Text(stringResource(Res.string.select_e_invoice_file), Modifier, Colors.CodinuxSecondaryColor, textAlign = TextAlign.Center, maxLines = 1)
            }
        }
    }


    lastExtractedEInvoice?.let {
        ViewInvoiceDialog(it) { lastExtractedEInvoice = null }
    }

}