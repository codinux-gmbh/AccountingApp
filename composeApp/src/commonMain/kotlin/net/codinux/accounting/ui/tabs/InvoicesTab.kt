package net.codinux.accounting.ui.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.codinux.accounting.resources.Res
import net.codinux.accounting.resources.create_invoice
import net.codinux.accounting.ui.composables.forms.SectionHeader
import net.codinux.accounting.ui.composables.invoice.InvoiceForm
import net.codinux.accounting.ui.composables.invoice.SelectEInvoiceFileToDisplay
import net.codinux.accounting.ui.extensions.rememberVerticalScroll

@Composable
fun InvoicesTab() {

    Column(Modifier.fillMaxWidth().rememberVerticalScroll()) {
        SelectEInvoiceFileToDisplay()


        Row(Modifier.padding(top = 32.dp, bottom = 2.dp)) {
            SectionHeader(Res.string.create_invoice, 18.sp)
        }

        InvoiceForm()
    }

}