package net.codinux.accounting.ui.composables.invoice

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.codinux.accounting.domain.invoice.model.HistoricalInvoiceData

@Preview
@Composable
fun InvoiceFormPreview() {
    InvoiceForm(HistoricalInvoiceData())
}