package net.codinux.accounting.domain.invoice.dataaccess

import net.codinux.accounting.domain.invoice.model.HistoricalInvoiceData

actual class InvoiceRepository {

    actual suspend fun loadHistoricalData(): HistoricalInvoiceData? = null

    actual suspend fun saveHistoricalData(data: HistoricalInvoiceData) {
        // no-op
    }

}