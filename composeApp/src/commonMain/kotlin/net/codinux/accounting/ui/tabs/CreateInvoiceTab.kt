package net.codinux.accounting.ui.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.codinux.accounting.ui.composables.invoice.InvoiceForm
import net.codinux.accounting.ui.extensions.rememberVerticalScroll

@Composable
fun CreateInvoiceTab() {

    Column(Modifier.fillMaxWidth().rememberVerticalScroll()) {
        InvoiceForm()
    }

}