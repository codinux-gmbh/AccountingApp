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
    var showAllPdfSettingsFields: Boolean = false,

    val selectedServiceDateOption: ServiceDateOptions = ServiceDateOptions.ServiceDate,
    val selectedEInvoiceFormat: EInvoiceFormat = EInvoiceFormat.FacturX,
    val selectedCreateEInvoiceOption: CreateEInvoiceOptions = CreateEInvoiceOptions.XmlOnly,
    val showGeneratedEInvoiceXml: Boolean = true,

    val lastXmlSaveDirectory: String? = null,
    val lastPdfSaveDirectory: String? = null,

    val lastOpenPdfDirectory: String? = null,
    val lastOpenLogoDirectory: String? = null,
) {
    override fun toString() = "$lastCreatedInvoice"
}