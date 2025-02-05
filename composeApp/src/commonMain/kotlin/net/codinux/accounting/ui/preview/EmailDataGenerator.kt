package net.codinux.accounting.ui.preview

import net.codinux.accounting.domain.common.extensions.toInstantAtSystemDefaultZone
import net.codinux.accounting.domain.mail.model.Email
import net.codinux.invoicing.email.model.ContentDisposition
import net.codinux.invoicing.email.model.EmailAddress
import net.codinux.invoicing.email.model.EmailAttachment
import net.codinux.invoicing.model.*
import net.codinux.invoicing.model.codes.*

object EmailDataGenerator {

    fun createMail(invoice: Invoice, messageId: Long = 1) =
        Email(messageId, messageId, messageId, invoice.supplier.email?.let { EmailAddress(it) }, "Rechnung ${invoice.details.invoiceNumber}", invoice.details.invoiceDate.toInstantAtSystemDefaultZone(),
            "Sehr geehrter Herr Sowieso,\nanbei unsere völlig überzogene Rechnung für unsere nutzlosen Dienstleistung mit Bitte um Überweisung innerhalb 24 Minuten.\nGezeichnet,\nHerr Geier",
            attachments = listOf(EmailAttachment("invoice.pdf", "pdf", null, ContentDisposition.Attachment, "application/pdf", null, MapInvoiceResult(invoice), null))
        )

}