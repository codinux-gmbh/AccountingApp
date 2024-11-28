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
import net.codinux.invoicing.email.model.EmailAttachment

@Composable
fun MailAttachmentListItem(attachment: EmailAttachment) {

    var showInvoice by remember { mutableStateOf(false) }


    RoundedCornersCard(Modifier.padding(start = 6.dp).widthIn(min = 70.dp).clickableWithHandCursorIf(attachment.invoice != null) { showInvoice = true }) {
        Text(attachment.filename, Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }


    if (showInvoice) {
        attachment.invoice?.let { invoice ->
            ViewInvoiceDialog(invoice) {
                showInvoice = false
            }
        }
    }
}

private fun Modifier.clickableWithHandCursorIf(condition: Boolean, onClick: () -> Unit): Modifier =
    if (condition) {
        // remove indication so that not ugly grey background gets displayed on hover
        this.handCursor().clickable(null, indication = null, onClick = onClick)
    } else {
        this
    }