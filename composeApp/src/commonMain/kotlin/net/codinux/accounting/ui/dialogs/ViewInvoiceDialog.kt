package net.codinux.accounting.ui.dialogs

import androidx.compose.runtime.Composable
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.invoice.InvoiceView
import net.codinux.invoicing.model.MapInvoiceResult
import org.jetbrains.compose.resources.stringResource

@Composable
fun ViewInvoiceDialog(invoice: MapInvoiceResult, onDismiss: () -> Unit) {

    BaseDialog(
        title = stringResource(Res.string.invoice_view),
        centerTitle = true,
        confirmButtonVisible = false,
        dismissButtonTitle = stringResource(Res.string.close),
        useMoreThanPlatformDefaultWidthOnSmallScreens = true,
        restrictMaxHeightForFullHeightDialogs = true,
        onDismiss = onDismiss
    ) {
        InvoiceView(invoice)
    }

}