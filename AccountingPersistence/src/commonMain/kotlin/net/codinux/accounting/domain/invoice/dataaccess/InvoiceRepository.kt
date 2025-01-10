package net.codinux.accounting.domain.invoice.dataaccess

import net.codinux.accounting.domain.invoice.model.CreateInvoiceSettings
import net.codinux.accounting.domain.invoice.model.ViewInvoiceSettings

interface InvoiceRepository {

    suspend fun loadViewInvoiceSettings(): ViewInvoiceSettings?

    suspend fun saveViewInvoiceSettings(settings: ViewInvoiceSettings)


    suspend fun loadCreateInvoiceSettings(): CreateInvoiceSettings?

    suspend fun saveCreateInvoiceSettings(settings: CreateInvoiceSettings)

}