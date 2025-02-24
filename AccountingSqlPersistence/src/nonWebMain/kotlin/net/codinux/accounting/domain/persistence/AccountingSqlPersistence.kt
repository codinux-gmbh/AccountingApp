package net.codinux.accounting.domain.persistence

import net.codinux.accounting.domain.invoice.dataaccess.*
import net.codinux.accounting.domain.ui.dataaccess.SqlUiStateRepository
import net.codinux.accounting.domain.ui.dataaccess.UiStateRepository
import net.codinux.accounting.persistence.AccountingDb

object AccountingSqlPersistence {

    private val schema = AccountingDb.Schema

    private val sqlDriver = AccountingPersistenceNonWeb.createSqlDriver("Accounting.db", schema)

    private val database = AccountingDb(sqlDriver)

    private val serializer = AccountingPersistence.serializer

    private val mapper = SqldelightMapper()


    val jsonDataStore = AccountingPersistenceNonWeb.getStorageForJsonDataFiles()

    val jsonInvoiceRepository = JsonInvoiceRepository(serializer, jsonDataStore)


    val sqlUiStateRepository: UiStateRepository = SqlUiStateRepository(database, mapper)

    val sqlInvoiceRepository: InvoiceRepository = SqlInvoiceRepository(database, serializer, mapper, jsonInvoiceRepository)

    val sqlInvoicePdfTemplateSettingsRepository: InvoicePdfTemplateSettingsRepository =
        SqlInvoicePdfTemplateSettingsRepository(database, mapper)

}