package net.codinux.accounting.domain.persistence

import net.codinux.accounting.domain.invoice.dataaccess.InvoiceRepository
import net.codinux.accounting.domain.invoice.dataaccess.SqlInvoiceRepository
import net.codinux.accounting.persistence.AccountingDb

object AccountingSqlPersistence {

    private val schema = AccountingDb.Schema

    private val sqlDriver = AccountingPersistenceNonWeb.createSqlDriver("Accounting.db", schema, 2L)

    private val database = AccountingDb(sqlDriver)

    private val serializer = AccountingPersistence.serializer


    val sqlInvoiceRepository: InvoiceRepository = SqlInvoiceRepository(database, serializer)

}