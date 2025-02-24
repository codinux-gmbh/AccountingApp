package net.codinux.accounting.domain.invoice.model

import kotlinx.serialization.Serializable
import net.codinux.invoicing.model.Invoice

@Serializable
data class RecentlyViewedInvoice(
    val path: String,
    val invoiceSummary: String? = null
) {
    constructor(path: String, invoice: Invoice?) : this(path, invoice?.shortDescription)

    override fun toString() = "$path${invoiceSummary?.let { " ($it)" } ?: ""}"
}