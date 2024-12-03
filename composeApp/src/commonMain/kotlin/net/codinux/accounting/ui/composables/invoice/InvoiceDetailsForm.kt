package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.codinux.accounting.resources.Res
import net.codinux.accounting.resources.invoice_date
import net.codinux.accounting.resources.invoice_number
import net.codinux.accounting.ui.composables.forms.datetime.DatePicker
import net.codinux.accounting.ui.composables.invoice.model.InvoiceDetailsViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun InvoiceDetailsForm(viewModel: InvoiceDetailsViewModel, isLargeDisplay: Boolean = false) {

    val invoiceDate by viewModel.invoiceDate.collectAsState()

    val invoiceNumber by viewModel.invoiceNumber.collectAsState()


    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        DatePicker(stringResource(Res.string.invoice_date), invoiceDate, Modifier.width(if (isLargeDisplay) 125.dp else 120.dp).fillMaxHeight(), true) { viewModel.invoiceDateChanged(it) }

        Spacer(Modifier.width(6.dp))

        InvoiceTextField(Res.string.invoice_number, invoiceNumber) { viewModel.invoiceNumberChanged(it) }
    }

}