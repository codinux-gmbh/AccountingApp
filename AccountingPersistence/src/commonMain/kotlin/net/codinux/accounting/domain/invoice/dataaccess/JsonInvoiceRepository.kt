package net.codinux.accounting.domain.invoice.dataaccess

import net.codinux.accounting.domain.invoice.model.CreateInvoiceSettings
import net.codinux.accounting.domain.invoice.model.ViewInvoiceSettings
import net.codinux.accounting.domain.serialization.DataStorage
import net.codinux.accounting.domain.serialization.JsonSerializer

class JsonInvoiceRepository(private val serializer: JsonSerializer, private val dateStorage: DataStorage) : InvoiceRepository {

    companion object {
        private const val ViewInvoiceSettingsStorageKey = "ViewInvoiceSettings"
        private const val CreateInvoiceSettingsStorageKey = "CreateInvoiceSettings"
    }


    override suspend fun loadViewInvoiceSettings(): ViewInvoiceSettings? =
        dateStorage.get(ViewInvoiceSettingsStorageKey)?.let { serializer.decode(it) }

    override suspend fun saveViewInvoiceSettings(settings: ViewInvoiceSettings) {
        dateStorage.store(ViewInvoiceSettingsStorageKey, serializer.encode(settings))
    }


    override suspend fun loadCreateInvoiceSettings(): CreateInvoiceSettings? =
        dateStorage.get(CreateInvoiceSettingsStorageKey)?.let { serializer.decode(it) }

    override suspend fun saveCreateInvoiceSettings(settings: CreateInvoiceSettings) {
        dateStorage.store(CreateInvoiceSettingsStorageKey, serializer.encode(settings))
    }

}