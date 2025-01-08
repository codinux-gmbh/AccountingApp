package net.codinux.accounting.domain.invoice.model

enum class CreateEInvoiceOptions {
    XmlOnly,

    CreateXmlAndAttachToExistingPdf,

    CreateXmlAndPdf
}