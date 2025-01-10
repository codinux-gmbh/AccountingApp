package net.codinux.accounting.domain.invoice.dataaccess

import net.codinux.accounting.domain.invoice.model.*
import net.codinux.accounting.domain.serialization.JsonSerializer
import net.codinux.accounting.persistence.AccountingDb
import net.codinux.invoicing.model.EInvoiceXmlFormat
import net.codinux.log.logger
import kotlin.enums.EnumEntries
import kotlin.jvm.JvmName

class SqlInvoiceRepository(database: AccountingDb, private val serializer: JsonSerializer) : InvoiceRepository {

    private val queries = database.invoiceQueries

    private val log by logger()


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
                                           showAllSupplierFields, showAllCustomerFields,
                                           selectedServiceDateOption, selectedEInvoiceXmlFormat, selectedCreateEInvoiceOption, showGeneratedEInvoiceXml,
                                           lastXmlSaveDirectory, lastPdfSaveDirectory, lastOpenFileDirectory ->
            CreateInvoiceSettings(
                lastCreatedInvoice?.let { serializer.decode(it) },

                showAllSupplierFields, showAllCustomerFields,

                mapToEnum(selectedServiceDateOption, ServiceDateOptions.entries),
                mapToEnum(selectedEInvoiceXmlFormat, EInvoiceXmlFormat.entries),
                mapToEnum(selectedCreateEInvoiceOption, CreateEInvoiceOptions.entries),
                showGeneratedEInvoiceXml,

                lastXmlSaveDirectory, lastPdfSaveDirectory, lastOpenFileDirectory
            )
        }.executeAsOneOrNull()


    override suspend fun saveCreateInvoiceSettings(settings: CreateInvoiceSettings) {
        queries.upsertCreateInvoiceSettings(
            serializer.encodeNullable(settings.lastCreatedInvoice),

            settings.showAllSupplierFields, settings.showAllCustomerFields,

            mapEnum(settings.selectedServiceDateOption), mapEnum(settings.selectedEInvoiceXmlFormat),
            mapEnum(settings.selectedCreateEInvoiceOption), settings.showGeneratedEInvoiceXml,

            settings.lastXmlSaveDirectory, settings.lastPdfSaveDirectory, settings.lastOpenFileDirectory
        )
    }


    private fun <E : Enum<E>> mapEnum(enum: Enum<E>): String = enum.name

    private fun <E : Enum<E>> mapToEnum(enumName: String, values: EnumEntries<E>): E =
        try {
            values.first { it.name == enumName }
        } catch (e: Throwable) {
            log.error(e) { "Could not map enumName '$enumName' to ${values.first()::class}"}
            throw e
        }


    private fun <E : Enum<E>> mapToEnum(enumName: String, values: EnumEntries<E>, enumNamesToMigrate: Map<String, String>): E =
        mapToEnum(enumNamesToMigrate[enumName] ?: enumName, values)

    private fun <E : Enum<E>> mapToEnumNullable(enumName: String, values: EnumEntries<E>): E? {
        val mapped = values.firstOrNull { it.name == enumName }

        if (mapped == null) {
            log.warn("Could not map '$enumName' to Enum ${values.first()::class}")
        }

        return mapped
    }


    @JvmName("mapIntNullable")
    private fun mapInt(int: Int?): Long? =
        int?.let { mapInt(it) }

    private fun mapInt(int: Int): Long = int.toLong()

    @JvmName("mapToIntNullable")
    private fun mapToInt(int: Long?): Int? =
        int?.let { mapToInt(it) }

    private fun mapToInt(int: Long): Int = int.toInt()

}