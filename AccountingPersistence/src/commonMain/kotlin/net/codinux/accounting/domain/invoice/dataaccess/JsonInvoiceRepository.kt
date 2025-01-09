package net.codinux.accounting.domain.invoice.dataaccess

import net.codinux.accounting.domain.invoice.model.HistoricalInvoiceData
import net.codinux.accounting.domain.serialization.DataStorage
import net.codinux.accounting.domain.serialization.JsonSerializer

class JsonInvoiceRepository(private val serializer: JsonSerializer, private val dateStorage: DataStorage) : InvoiceRepository {

    companion object {
        private const val StorageKey = "CreateInvoiceSettings"
    }


    override suspend fun loadHistoricalData(): HistoricalInvoiceData? =
        dateStorage.get(StorageKey)?.let { serializer.decode(it) }

    override suspend fun saveHistoricalData(data: HistoricalInvoiceData) {
        dateStorage.store(StorageKey, serializer.encode(data))
    }

}