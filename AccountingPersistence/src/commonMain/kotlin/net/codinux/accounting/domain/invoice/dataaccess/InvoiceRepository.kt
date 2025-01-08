package net.codinux.accounting.domain.invoice.dataaccess

import net.codinux.accounting.domain.invoice.model.HistoricalInvoiceData

interface InvoiceRepository2 {

    suspend fun loadHistoricalData(): HistoricalInvoiceData?

    suspend fun saveHistoricalData(data: HistoricalInvoiceData)

}