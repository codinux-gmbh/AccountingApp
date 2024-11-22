package net.codinux.accounting.ui.composables.mail

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import net.codinux.accounting.ui.preview.DataGenerator
import java.time.LocalDate

@Preview
@Composable
fun MailsListPreview() {
    MailsList(listOf(
        DataGenerator.createMail(DataGenerator.createInvoice(), 2),
        DataGenerator.createMail(DataGenerator.createInvoice("RE-789", LocalDate.of(2015, 10, 21)), 1)
    ))
}