package net.codinux.accounting.ui.composables.mail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.codinux.accounting.domain.mail.model.Email
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.extensions.horizontalScroll
import net.codinux.invoicing.email.model.EmailAttachment

private val formatUtil = DI.formatUtil

@Composable
fun MailListItem(mail: Email) {

    val backgroundColor = Color.White


    // height 64 dp: first row has 24 dp (due to attachment icon), 6 dp vertical spacing, and each line in the second row has 17 dp. But don't know why + 1 dp is needed so that second body line gets displayed
    Column(Modifier.fillMaxWidth().background(backgroundColor).padding(horizontal = 6.dp, vertical = 6.dp).height(101.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(mail.sender?.toString() ?: "", Modifier.widthIn(20.dp, 175.dp), fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)

            Text("-", Modifier.padding(horizontal = 4.dp))

            Text(mail.subject, Modifier.weight(1f), fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)

            Text(formatUtil.formatDateToDayAndMonth(mail.date), fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(6.dp))

        Row(Modifier.fillMaxWidth().weight(1f), verticalAlignment = Alignment.CenterVertically) {
            val body = (mail.bodyPreviewText).replace("\r", "").replace("\n", " ")
            Text(body, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }

        Spacer(Modifier.height(6.dp))

        Row(Modifier.fillMaxWidth().height(36.dp).horizontalScroll(), verticalAlignment = Alignment.CenterVertically) {
            if (mail.attachments.isNotEmpty()) {
                Icon(Icons.Outlined.Attachment, "Mail has attachment(s)")
            }

            mail.attachments.sortedWith(compareByDescending<EmailAttachment> { it.containsEInvoice }.thenByDescending { it.couldExtractPdfInvoiceData }.thenBy { it.filename.lowercase() } ).forEach {
                MailAttachmentListItem(it)
            }
        }
    }

}