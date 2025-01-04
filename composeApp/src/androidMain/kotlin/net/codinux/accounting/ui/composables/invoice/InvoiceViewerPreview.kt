package net.codinux.accounting.ui.composables.invoice

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.codinux.accounting.ui.preview.DataGenerator
import net.codinux.invoicing.model.MapInvoiceResult

@Preview
@Composable
fun InvoiceViewerPreview() {
    InvoiceView(MapInvoiceResult(DataGenerator.createInvoice()))
}