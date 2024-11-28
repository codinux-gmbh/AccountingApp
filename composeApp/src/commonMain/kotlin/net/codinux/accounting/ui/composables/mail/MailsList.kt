package net.codinux.accounting.ui.composables.mail

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import net.codinux.accounting.domain.mail.model.Email
import net.codinux.accounting.ui.composables.ItemDivider
import net.codinux.accounting.ui.config.Style

@Composable
fun MailsList(mails: List<Email>) {

    LazyColumn(Modifier.padding(vertical = Style.MainScreenTabVerticalPadding)) {
        itemsIndexed(mails) { index, mail ->
            key(mail.id) {
                MailListItem(mail)

                if (index < mails.size - 1) {
                    ItemDivider()
                }
            }
        }
    }

}