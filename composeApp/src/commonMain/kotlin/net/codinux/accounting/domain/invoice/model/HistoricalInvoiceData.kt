package net.codinux.accounting.domain.invoice.model

import net.codinux.invoicing.model.EInvoiceXmlFormat
import net.codinux.invoicing.model.Invoice

class HistoricalInvoiceData(
    val lastCreatedInvoice: Invoice? = null,

    val selectedServiceDateOption: ServiceDateOptions = ServiceDateOptions.ServiceDate,
    val selectedEInvoiceXmlFormat: EInvoiceXmlFormat = EInvoiceXmlFormat.FacturX,
    val selectedCreateEInvoiceOption: CreateEInvoiceOptions = CreateEInvoiceOptions.XmlOnly,
    val showGeneratedEInvoiceXml: Boolean = true,

    // TODO: currently not used
    val lastXmlSaveDirectory: String? = null,
    val lastPdfSaveDirectory: String? = null,

    val lastOpenFileDirectory: String? = null,
) {
    override fun toString() = "$lastCreatedInvoice"
}