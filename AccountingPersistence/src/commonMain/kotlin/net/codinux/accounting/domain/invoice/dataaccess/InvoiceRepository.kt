package net.codinux.accounting.domain.invoice.dataaccess

import net.codinux.accounting.domain.invoice.model.CreateInvoiceSettings

interface InvoiceRepository {

    suspend fun loadCreateInvoiceSettings(): CreateInvoiceSettings?

    suspend fun saveCreateInvoiceSettings(settings: CreateInvoiceSettings)

}