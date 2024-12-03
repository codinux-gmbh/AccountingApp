package net.codinux.accounting.ui.composables.invoice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.invoice.model.BankDetailsViewModel

@Composable
fun BankDetailsForm(viewModel: BankDetailsViewModel) {

    val accountHolderName by viewModel.accountHolderName.collectAsState()

    val bankName by viewModel.bankName.collectAsState()

    val accountNumber by viewModel.accountNumber.collectAsState()

    val bankCode by viewModel.bankCode.collectAsState()


    InvoiceTextField(Res.string.account_holder_if_different, accountHolderName) { viewModel.accountHolderNameChanged(it) }

    InvoiceTextField(Res.string.name_of_financial_institution, bankName) { viewModel.bankNameChanged(it) }

    InvoiceTextField(Res.string.iban, accountNumber) { viewModel.accountNumberChanged(it) }

    InvoiceTextField(Res.string.bic, bankCode) { viewModel.bankCodeChanged(it) }

}