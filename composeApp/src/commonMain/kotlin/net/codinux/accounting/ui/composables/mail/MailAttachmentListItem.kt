package net.codinux.accounting.ui.composables.mail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.composables.forms.RoundedCornersCard
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.dialogs.PdfInvoiceDataDialog
import net.codinux.accounting.ui.dialogs.ViewInvoiceDialog
import net.codinux.accounting.ui.extensions.handCursor
import net.codinux.invoicing.email.model.EmailAttachment


private val formatUtil = DI.formatUtil

@Composable
fun MailAttachmentListItem(attachment: EmailAttachment) {

    val invoice = attachment.invoice

    var showInvoice by remember { mutableStateOf(false) }


    RoundedCornersCard(Modifier.padding(start = 6.dp).widthIn(min = 70.dp).clickableWithHandCursorIf(attachment.containsEInvoice || attachment.couldExtractPdfInvoiceData) { showInvoice = true }) {
        var displayText = attachment.filename

        invoice?.totals?.duePayableAmount?.let { total ->
            displayText += " (${formatUtil.formatAmountOfMoney(total, invoice?.details?.currency, true)})"
        }
        if (invoice?.totals?.duePayableAmount == null) {
            attachment.pdfInvoiceData?.potentialTotalAmount?.let { total ->
                displayText += " (${formatUtil.formatAmountOfMoney(total.amount, total.currency, true)})"
            }
        }

        Text(displayText, Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }


    if (showInvoice) {
        attachment.mapInvoiceResult?.let { invoice ->
            ViewInvoiceDialog(invoice) {
                showInvoice = false
            }
        }

        if (invoice == null) {
            attachment.pdfInvoiceData?.let { pdfInvoiceData ->
                PdfInvoiceDataDialog(pdfInvoiceData) {
                    showInvoice = false
                }
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