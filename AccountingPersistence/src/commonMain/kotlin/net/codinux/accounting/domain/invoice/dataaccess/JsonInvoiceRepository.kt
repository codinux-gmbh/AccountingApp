package net.codinux.accounting.domain.invoice.dataaccess

import net.codinux.accounting.domain.invoice.model.CreateInvoiceSettings
import net.codinux.accounting.domain.serialization.DataStorage
import net.codinux.accounting.domain.serialization.JsonSerializer

class JsonInvoiceRepository(private val serializer: JsonSerializer, private val dateStorage: DataStorage) : InvoiceRepository {

    companion object {
        private const val StorageKey = "CreateInvoiceSettings"
    }


    override suspend fun loadCreateInvoiceSettings(): CreateInvoiceSettings? =
        dateStorage.get(StorageKey)?.let { serializer.decode(it) }

    override suspend fun saveCreateInvoiceSettings(settings: CreateInvoiceSettings) {
        dateStorage.store(StorageKey, serializer.encode(settings))
    }

}