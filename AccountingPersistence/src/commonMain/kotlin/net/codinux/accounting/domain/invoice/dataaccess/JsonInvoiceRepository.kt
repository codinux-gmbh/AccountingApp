package net.codinux.accounting.domain.invoice.dataaccess

import net.codinux.accounting.domain.invoice.model.CreateInvoiceSettings
import net.codinux.accounting.domain.invoice.model.RecentlyViewedInvoice
import net.codinux.accounting.domain.invoice.model.ViewInvoiceSettings
import net.codinux.accounting.domain.serialization.DataStorage
import net.codinux.accounting.domain.serialization.JsonSerializer

open class JsonInvoiceRepository(private val serializer: JsonSerializer, private val dateStorage: DataStorage) : InvoiceRepository {

    companion object {
        const val ViewInvoiceSettingsStorageKey = "ViewInvoiceSettings.json"
        const val CreateInvoiceSettingsStorageKey = "CreateInvoiceSettings.json"
        const val RecentlyViewedInvoicesStorageKey = "RecentlyViewedInvoices.json"
    }


    // in this case we keep a reference on once loaded RecentlyViewedInvoices as UI keeps them in memory anyway
    protected var recentlyViewedInvoices: MutableList<RecentlyViewedInvoice> = mutableListOf()


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


    override fun loadRecentlyViewedInvoices(): List<RecentlyViewedInvoice> =
        loadRecentlyViewedInvoicesFromStorageOrEmpty().also {
            this.recentlyViewedInvoices = it.toMutableList()
        }

    protected open fun loadRecentlyViewedInvoicesFromStorageOrEmpty(): List<RecentlyViewedInvoice>  =
        dateStorage.get(RecentlyViewedInvoicesStorageKey)?.let { serializer.decode(it) }
            ?: emptyList()

    override fun addRecentlyViewedInvoice(viewedInvoice: RecentlyViewedInvoice, countMaxStoredRecentlyViewedInvoices: Int) {
        recentlyViewedInvoices.add(0, viewedInvoice)

        if (recentlyViewedInvoices.size > countMaxStoredRecentlyViewedInvoices) {
            recentlyViewedInvoices = recentlyViewedInvoices.take(countMaxStoredRecentlyViewedInvoices).toMutableList()
        }

        dateStorage.store(RecentlyViewedInvoicesStorageKey, serializer.encode(recentlyViewedInvoices))
    }

}