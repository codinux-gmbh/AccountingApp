package net.codinux.accounting.ui.dialogs

import androidx.compose.runtime.Composable
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.invoice.PdfInvoiceDataView
import net.codinux.accounting.ui.config.Colors
import net.codinux.invoicing.pdf.PdfInvoiceData
import org.jetbrains.compose.resources.stringResource

@Composable
fun PdfInvoiceDataDialog(pdfInvoiceData: PdfInvoiceData, onDismiss: () -> Unit) {

    BaseDialog(
        title = stringResource(Res.string.invoice_view),
        centerTitle = true,
        confirmButtonVisible = false,
        dismissButtonTitle = stringResource(Res.string.close),
        useMoreThanPlatformDefaultWidthOnSmallScreens = true,
        restrictMaxHeightForFullHeightDialogs = true,
        backgroundColor = Colors.MainBackgroundColor,
        onDismiss = onDismiss
    ) {
        PdfInvoiceDataView(pdfInvoiceData)
    }

}