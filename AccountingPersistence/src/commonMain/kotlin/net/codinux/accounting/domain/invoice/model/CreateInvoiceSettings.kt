package net.codinux.accounting.domain.invoice.model

import kotlinx.serialization.Serializable
import net.codinux.invoicing.format.EInvoiceFormat
import net.codinux.invoicing.model.Invoice

@Serializable
data class CreateInvoiceSettings(
    val lastCreatedInvoice: Invoice? = null,

    var showAllSupplierFields: Boolean = false,
    var showAllCustomerFields: Boolean = false,
    var showAllBankDetailsFields: Boolean = false,

    val selectedServiceDateOption: ServiceDateOptions = ServiceDateOptions.ServiceDate,
    val selectedEInvoiceFormat: EInvoiceFormat = EInvoiceFormat.FacturX,
    val selectedCreateEInvoiceOption: CreateEInvoiceOptions = CreateEInvoiceOptions.XmlOnly,
    val showGeneratedEInvoiceXml: Boolean = true,

    // TODO: currently not used
    val lastXmlSaveDirectory: String? = null,
    val lastPdfSaveDirectory: String? = null,

    val lastOpenFileDirectory: String? = null,
) {
    override fun toString() = "$lastCreatedInvoice"
}