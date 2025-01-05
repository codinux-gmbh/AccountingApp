package net.codinux.accounting.ui.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.composables.ComposableOfMaxWidth
import net.codinux.accounting.ui.composables.invoice.InvoiceView
import net.codinux.accounting.ui.composables.invoice.SelectEInvoiceFileToDisplay
import net.codinux.accounting.ui.extensions.verticalScroll
import net.codinux.invoicing.reader.ReadEInvoiceFileResult

@Composable
fun ViewInvoiceTab() {

    var selectedInvoice by remember { mutableStateOf<ReadEInvoiceFileResult?>(null) }

    var selectedInvoiceXml by remember { mutableStateOf<String?>(null) }


    Column(Modifier.fillMaxSize().verticalScroll(), verticalArrangement = Arrangement.Center) {
        val invoice = selectedInvoice?.mapInvoiceResult

        ComposableOfMaxWidth(600.dp) {
            SelectEInvoiceFileToDisplay { invoice, xml ->
                selectedInvoice = invoice
                selectedInvoiceXml = xml
            }

            if (invoice != null) {
                Row(Modifier.padding(top = 32.dp)) {
                    InvoiceView(invoice, selectedInvoice?.readPdfResult, selectedInvoiceXml, false)
                }
            }
        }
    }

}