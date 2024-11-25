package net.codinux.accounting.ui.composables.invoice

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.codinux.accounting.ui.preview.DataGenerator

@Preview
@Composable
fun InvoiceViewerPreview() {
    InvoiceView(DataGenerator.createInvoice())
}