package net.codinux.accounting.domain.invoice.service

import net.codinux.accounting.domain.invoice.model.EInvoiceXmlFormat
import net.codinux.invoicing.creation.EInvoiceCreator
import net.codinux.invoicing.model.Invoice

class InvoiceService(
    private val creator: EInvoiceCreator = EInvoiceCreator()
) {

    fun createEInvoiceXml(invoice: Invoice, selectedFormat: EInvoiceXmlFormat): String = when (selectedFormat) {
        EInvoiceXmlFormat.FacturX -> creator.createFacturXXml(invoice)
        EInvoiceXmlFormat.XRechnung -> creator.createXRechnungXml(invoice)
    }

}