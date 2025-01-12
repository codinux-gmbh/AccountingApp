package net.codinux.accounting.domain.persistence

import net.codinux.accounting.domain.invoice.dataaccess.InvoiceRepository
import net.codinux.accounting.domain.invoice.dataaccess.SqlInvoiceRepository
import net.codinux.accounting.domain.ui.dataaccess.SqlUiStateRepository
import net.codinux.accounting.domain.ui.dataaccess.UiStateRepository
import net.codinux.accounting.persistence.AccountingDb

object AccountingSqlPersistence {

    private val schema = AccountingDb.Schema

    private val sqlDriver = AccountingPersistenceNonWeb.createSqlDriver("Accounting.db", schema, 2L)

    private val database = AccountingDb(sqlDriver)

    private val serializer = AccountingPersistence.serializer

    private val mapper = SqldelightMapper()


    val sqlUiStateRepository: UiStateRepository = SqlUiStateRepository(database, mapper)

    val sqlInvoiceRepository: InvoiceRepository = SqlInvoiceRepository(database, serializer, mapper)

}