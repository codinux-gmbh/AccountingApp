package net.codinux.accounting.domain.invoice.service

import net.codinux.invoicing.creation.EInvoiceCreator
import net.codinux.invoicing.model.Invoice

class InvoiceService(
    private val creator: EInvoiceCreator = EInvoiceCreator()
) {

    fun createEInvoiceXml(invoice: Invoice): String {
        return creator.createFacturXXml(invoice)
    }

}