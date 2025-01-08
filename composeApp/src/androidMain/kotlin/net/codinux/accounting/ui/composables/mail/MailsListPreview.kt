package net.codinux.accounting.ui.composables.mail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.codinux.accounting.domain.testdata.DataGenerator
import net.codinux.accounting.ui.preview.EmailDataGenerator
import net.codinux.accounting.ui.state.EmailsUiState
import net.codinux.invoicing.model.LocalDate

@Preview
@Composable
fun MailsListPreview() {
    MailsList(
        EmailsUiState(),
        listOf(
            EmailDataGenerator.createMail(DataGenerator.createInvoice(), 2),
            EmailDataGenerator.createMail(DataGenerator.createInvoice("RE-789", LocalDate(2015, 10, 21)), 1)
        )
    )
}