package net.codinux.accounting.ui.composables.mail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.composables.forms.RoundedCornersCard
import net.codinux.accounting.ui.dialogs.ViewInvoiceDialog
import net.codinux.accounting.ui.extensions.handCursor
import net.codinux.invoicing.mail.MailAttachmentWithEInvoice

@Composable
fun MailAttachmentListItem(attachment: MailAttachmentWithEInvoice) {

    var showInvoice by remember { mutableStateOf(false) }


    RoundedCornersCard(Modifier.padding(start = 6.dp).widthIn(min = 70.dp)
        // remove indication so that not ugly grey background gets displayed on hover
        .let { if (attachment.invoice != null) it.handCursor().clickable(null, indication = null) { showInvoice = true } else it }) {
        Text(attachment.filename, Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }


    if (showInvoice) {
        ViewInvoiceDialog(attachment.invoice) {
            showInvoice = false
        }
    }
}