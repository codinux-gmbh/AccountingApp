package net.codinux.accounting.ui.dialogs

import androidx.compose.runtime.Composable
import net.codinux.accounting.resources.Res
import net.codinux.accounting.resources.invoice_view
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
        useMoreThanPlatformDefaultWidthOnMobile = true,
        backgroundColor = Colors.MainBackgroundColor,
        onDismiss = onDismiss
    ) {
        PdfInvoiceDataView(pdfInvoiceData)
    }

}