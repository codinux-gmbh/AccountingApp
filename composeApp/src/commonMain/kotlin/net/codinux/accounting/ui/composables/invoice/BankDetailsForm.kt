package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.invoice.model.BankDetailsViewModel
import net.codinux.accounting.ui.config.Style

@Composable
fun BankDetailsForm(viewModel: BankDetailsViewModel) {

    val accountHolderName by viewModel.accountHolderName.collectAsState()

    val bankName by viewModel.bankName.collectAsState()

    val accountNumber by viewModel.accountNumber.collectAsState()

    val bankCode by viewModel.bankCode.collectAsState()


    InvoiceTextField(Res.string.account_holder_if_different, accountHolderName) { viewModel.accountHolderNameChanged(it) }

    InvoiceTextField(Res.string.name_of_financial_institution, bankName) { viewModel.bankNameChanged(it) }

    Row(Modifier.fillMaxWidth().padding(top = Style.FormVerticalRowPadding)) {
        InvoiceTextField(Res.string.iban, accountNumber, modifier = Modifier.weight(1f)) { viewModel.accountNumberChanged(it) }

        InvoiceTextField(Res.string.bic, bankCode, modifier = Modifier.width(140.dp).padding(start = 6.dp)) { viewModel.bankCodeChanged(it) }
    }

}