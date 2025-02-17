package net.codinux.accounting.domain.invoice.dataaccess

import net.codinux.accounting.domain.serialization.DataStorage
import net.codinux.accounting.domain.serialization.JsonSerializer
import net.codinux.invoicing.pdf.InvoicePdfTemplateSettings

class JsonInvoicePdfTemplateSettingsRepository(private val serializer: JsonSerializer, private val dateStorage: DataStorage)
    : InvoicePdfTemplateSettingsRepository {

    companion object {
        private const val InvoicePdfTemplateSettingsStorageKey = "InvoicePdfTemplateSettings"
    }


    override fun loadInvoicePdfTemplateSettings(): InvoicePdfTemplateSettings? =
        dateStorage.get(InvoicePdfTemplateSettingsStorageKey)?.let { serializer.decode(it) }

    override suspend fun saveInvoicePdfTemplateSettings(settings: InvoicePdfTemplateSettings) {
        dateStorage.store(InvoicePdfTemplateSettingsStorageKey, serializer.encode(settings))
    }

}