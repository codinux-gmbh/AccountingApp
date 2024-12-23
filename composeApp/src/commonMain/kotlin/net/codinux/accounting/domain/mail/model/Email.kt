package net.codinux.accounting.domain.mail.model

import net.codinux.invoicing.email.model.EmailAddress
import net.codinux.invoicing.email.model.EmailAttachment
import net.codinux.invoicing.model.Instant

class Email(
    val id: Long,
    val emailAccountId: Long,

    val messageId: Long,

    val sender: EmailAddress?,
    val subject: String,
    val date: Instant,

    val bodyPreviewText: String,

    val contentLanguage: String? = null,
    val isEncrypted: Boolean = false,

    val attachments: List<EmailAttachment> = emptyList()
) {
    val hasAttachments: Boolean by lazy { attachments.isNotEmpty() }

    val hasEInvoiceAttachment: Boolean by lazy { attachments.any { it.containsEInvoice } }

    val hasPdfAttachment: Boolean by lazy { attachments.any { it.isPdfFile } }

    override fun toString() = "${date.toLocalDateAtSystemDefaultZone()} $sender: $subject, ${attachments.size} attachment(s)"
}