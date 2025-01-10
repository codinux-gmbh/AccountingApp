package net.codinux.accounting.domain.invoice.model

import kotlinx.serialization.Serializable

@Serializable
data class ViewInvoiceSettings(
    var lastSelectedInvoiceFile: String? = null,
    var showInvoiceXml: Boolean = false,
    var showPdfDetails: Boolean = false,
    var showEpcQrCode: Boolean = false,
)