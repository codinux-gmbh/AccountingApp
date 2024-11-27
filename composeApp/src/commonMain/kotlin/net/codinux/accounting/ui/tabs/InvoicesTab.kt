package net.codinux.accounting.ui.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import net.codinux.accounting.ui.composables.invoice.InvoiceForm
import net.codinux.accounting.ui.state.UiState

@Composable
fun InvoicesTab(uiState: UiState) {

    val historicalData = uiState.historicalInvoiceData.collectAsState().value


    InvoiceForm(historicalData)

}