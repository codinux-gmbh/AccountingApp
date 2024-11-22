package net.codinux.accounting.ui.composables.mail

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import net.codinux.accounting.ui.composables.ItemDivider
import net.codinux.invoicing.mail.MailWithInvoice

@Composable
fun MailsList(mails: List<MailWithInvoice>) {

    LazyColumn(Modifier) {
        itemsIndexed(mails) { index, mail ->
            key(mail.messageNumber) {
                MailListItem(mail)

                if (index < mails.size - 1) {
                    ItemDivider()
                }
            }
        }
    }

}