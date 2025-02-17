package net.codinux.accounting.domain.invoice.dataaccess

import net.codinux.invoicing.pdf.InvoicePdfTemplateSettings

interface InvoicePdfTemplateSettingsRepository {

    fun loadInvoicePdfTemplateSettings(): InvoicePdfTemplateSettings?

    suspend fun saveInvoicePdfTemplateSettings(settings: InvoicePdfTemplateSettings)

}