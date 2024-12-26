package net.codinux.accounting.domain.invoice.dataaccess

import net.codinux.accounting.domain.invoice.model.HistoricalInvoiceData

expect class InvoiceRepository {

    /**
     * Does not handle errors, [net.codinux.accounting.domain.invoice.service.InvoiceService] does this for us
     */
    suspend fun loadHistoricalData(): HistoricalInvoiceData?

    suspend fun saveHistoricalData(data: HistoricalInvoiceData)

}