package net.codinux.accounting.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.codinux.accounting.ui.composables.invoice.SelectEInvoiceFileToDisplay

@Composable
fun ViewInvoiceTab() {

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        SelectEInvoiceFileToDisplay()
    }

}