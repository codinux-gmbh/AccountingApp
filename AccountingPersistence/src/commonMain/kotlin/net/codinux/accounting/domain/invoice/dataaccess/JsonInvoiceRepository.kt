package net.codinux.accounting.domain.invoice.dataaccess

import net.codinux.accounting.domain.invoice.model.HistoricalInvoiceData
import net.codinux.accounting.domain.serialization.DataStorage
import net.codinux.accounting.domain.serialization.JsonSerializer

class JsonInvoiceRepository(private val serializer: JsonSerializer, private val dateStorage: DataStorage) : InvoiceRepository {

    companion object {
        private val Key = "CreateInvoiceSettings"
    }


    override suspend fun loadHistoricalData(): HistoricalInvoiceData? =
        dateStorage.get(Key)?.let { serializer.decode(it) }

    override suspend fun saveHistoricalData(data: HistoricalInvoiceData) {
        dateStorage.store(Key, serializer.encode(data))
    }

}