package net.codinux.accounting.domain.invoice.dataaccess

import net.codinux.accounting.domain.invoice.model.*
import net.codinux.accounting.domain.persistence.SqldelightMapper
import net.codinux.accounting.domain.serialization.JsonSerializer
import net.codinux.accounting.persistence.AccountingDb
import net.codinux.invoicing.format.EInvoiceFormat

class SqlInvoiceRepository(
    database: AccountingDb,
    private val serializer: JsonSerializer,
    private val mapper: SqldelightMapper,
    private val jsonInvoiceRepository: JsonInvoiceRepository
) : InvoiceRepository {

    private val queries = database.invoiceQueries


    override suspend fun loadViewInvoiceSettings(): ViewInvoiceSettings? =
        queries.getViewInvoiceSettings { _, lastSelectedInvoiceFile, showInvoiceXml, showPdfDetails, showEpcQrCode ->
            ViewInvoiceSettings(lastSelectedInvoiceFile, showInvoiceXml, showPdfDetails, showEpcQrCode)
        }.executeAsOneOrNull()

    override suspend fun saveViewInvoiceSettings(settings: ViewInvoiceSettings) {
        queries.upsertViewInvoiceSettings(
            settings.lastSelectedInvoiceFile,

            settings.showInvoiceXml,
            settings.showPdfDetails,
            settings.showEpcQrCode
        )
    }


    override suspend fun loadCreateInvoiceSettings(): CreateInvoiceSettings? =
        queries.getCreateInvoiceSettings { _, lastCreatedInvoice,
                                           showAllSupplierFields, showAllCustomerFields, showAllBankDetailsFields, showAllPdfSettingsFields,
                                           selectedServiceDateOption, selectedEInvoiceFormat, selectedCreateEInvoiceOption, showGeneratedEInvoiceXml,
                                           lastXmlSaveDirectory, lastPdfSaveDirectory, lastOpenPdfDirectory, lastOpenLogoDirectory ->
            CreateInvoiceSettings(
                lastCreatedInvoice?.let { serializer.decode(it) },

                showAllSupplierFields, showAllCustomerFields, showAllBankDetailsFields, showAllPdfSettingsFields,

                mapper.mapToEnum(selectedServiceDateOption, ServiceDateOptions.entries),
                mapper.mapToEnum(selectedEInvoiceFormat, EInvoiceFormat.entries),
                mapper.mapToEnum(selectedCreateEInvoiceOption, CreateEInvoiceOptions.entries),
                showGeneratedEInvoiceXml,

                lastXmlSaveDirectory, lastPdfSaveDirectory, lastOpenPdfDirectory, lastOpenLogoDirectory
            )
        }.executeAsOneOrNull()


    override suspend fun saveCreateInvoiceSettings(settings: CreateInvoiceSettings) {
        queries.upsertCreateInvoiceSettings(
            serializer.encodeNullable(settings.lastCreatedInvoice),

            settings.showAllSupplierFields, settings.showAllCustomerFields,
            settings.showAllBankDetailsFields, settings.showAllPdfSettingsFields,

            mapper.mapEnum(settings.selectedServiceDateOption), mapper.mapEnum(settings.selectedEInvoiceFormat),
            mapper.mapEnum(settings.selectedCreateEInvoiceOption), settings.showGeneratedEInvoiceXml,

            settings.lastXmlSaveDirectory, settings.lastPdfSaveDirectory,
            settings.lastOpenPdfDirectory, settings.lastOpenLogoDirectory
        )
    }


    /*      we store non-mass data now in JSON files        */

    override fun loadRecentlyViewedInvoices() = jsonInvoiceRepository.loadRecentlyViewedInvoices()

    override fun addRecentlyViewedInvoice(viewedInvoice: RecentlyViewedInvoice, countMaxStoredRecentlyViewedInvoices: Int) =
        jsonInvoiceRepository.addRecentlyViewedInvoice(viewedInvoice, countMaxStoredRecentlyViewedInvoices)

}