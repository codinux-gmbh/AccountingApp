package net.codinux.accounting.ui.composables.mail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.codinux.accounting.domain.mail.model.Email
import net.codinux.accounting.ui.composables.ItemDivider
import net.codinux.accounting.ui.composables.VerticalScrollbar
import net.codinux.accounting.ui.config.Style
import net.codinux.accounting.ui.state.EmailsUiState

@Composable
fun MailsList(uiState: EmailsUiState, mails: List<Email>) {

    val showEmailFilterPanel = uiState.showEmailFilterPanel.collectAsState().value

    val showOnlyEmailsWithInvoices = uiState.showOnlyEmailsWithInvoices.collectAsState().value

    val scrollState = rememberLazyListState()


    fun showEmail(email: Email): Boolean =
        if (showOnlyEmailsWithInvoices) email.hasPdfAttachment || email.hasEInvoiceAttachment
        else true


    Box(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.padding(vertical = Style.MainScreenTabVerticalPadding), scrollState) {
            itemsIndexed(mails.filter { showEmail(it) }) { index, mail ->
                key(mail.id) {
                    MailListItem(mail)

                    if (index < mails.size - 1) {
                        ItemDivider()
                    }
                }
            }
        }

        VerticalScrollbar(scrollState, Modifier.align(Alignment.CenterEnd).fillMaxHeight())

        if (showEmailFilterPanel) {
            EmailFilterPanel(uiState)
        }
    }

}