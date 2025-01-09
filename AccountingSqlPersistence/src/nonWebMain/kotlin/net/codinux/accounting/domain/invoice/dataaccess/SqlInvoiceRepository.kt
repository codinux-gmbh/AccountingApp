package net.codinux.accounting.domain.invoice.dataaccess

import net.codinux.accounting.persistence.AccountingDb
import net.codinux.invoicing.model.EInvoiceXmlFormat
import net.codinux.log.logger
import kotlin.enums.EnumEntries
import kotlin.js.JsName
import kotlin.jvm.JvmName

class SqlInvoiceRepository(database: AccountingDb, private val serializer: JsonSerializer) : InvoiceRepository2 {

    private val queries = database.invoiceQueries

    private val log by logger()


    override suspend fun loadHistoricalData(): HistoricalInvoiceData? =
        queries.getCreateInvoiceSettings { _, lastCreatedInvoice,
                                           selectedServiceDateOption, selectedEInvoiceXmlFormat, selectedCreateEInvoiceOption, showGeneratedEInvoiceXml,
                                           lastXmlSaveDirectory, lastPdfSaveDirectory, lastOpenFileDirectory ->
            HistoricalInvoiceData(
                lastCreatedInvoice?.let { serializer.decode(it) },

                mapToEnum(selectedServiceDateOption, ServiceDateOptions.entries),
                mapToEnum(selectedEInvoiceXmlFormat, EInvoiceXmlFormat.entries),
                mapToEnum(selectedCreateEInvoiceOption, CreateEInvoiceOptions.entries),
                showGeneratedEInvoiceXml,

                lastXmlSaveDirectory, lastPdfSaveDirectory, lastOpenFileDirectory
            )
        }.executeAsOneOrNull()


    override suspend fun saveHistoricalData(data: HistoricalInvoiceData) {
        queries.upsertCreateInvoiceSettings(
            serializer.encodeNullable(data.lastCreatedInvoice),

            mapEnum(data.selectedServiceDateOption), mapEnum(data.selectedEInvoiceXmlFormat),
            mapEnum(data.selectedCreateEInvoiceOption), data.showGeneratedEInvoiceXml,

            data.lastXmlSaveDirectory, data.lastPdfSaveDirectory, data.lastOpenFileDirectory
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
    @JsName("mapIntNullable")
    private fun mapInt(int: Int?): Long? =
        int?.let { mapInt(it) }

    private fun mapInt(int: Int): Long = int.toLong()

    @JvmName("mapToIntNullable")
    @JsName("mapToIntNullable")
    private fun mapToInt(int: Long?): Int? =
        int?.let { mapToInt(it) }

    private fun mapToInt(int: Long): Int = int.toInt()

}