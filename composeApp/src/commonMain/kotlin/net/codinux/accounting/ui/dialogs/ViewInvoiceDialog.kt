package net.codinux.accounting.ui.dialogs

import androidx.compose.runtime.Composable
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.invoice.InvoiceView
import net.codinux.accounting.ui.config.Colors
import net.codinux.invoicing.model.Invoice
import org.jetbrains.compose.resources.stringResource

@Composable
fun ViewInvoiceDialog(invoice: Invoice, onDismiss: () -> Unit) {

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
        InvoiceView(invoice)
    }

}