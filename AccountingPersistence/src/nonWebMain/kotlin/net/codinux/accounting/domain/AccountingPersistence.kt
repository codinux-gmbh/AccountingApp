package net.codinux.accounting.domain

import net.codinux.accounting.domain.invoice.dataaccess.InvoiceRepository2
import net.codinux.accounting.domain.invoice.dataaccess.SqlInvoiceRepository
import net.codinux.accounting.domain.serialization.JsonSerializer
import net.codinux.accounting.persistence.AccountingDb

object AccountingPersistence {

    private val schema = AccountingDb.Schema

    private val sqlDriver = AccountingPersistenceNonWeb.createSqlDriver("Accounting.db", schema, 2L)

    private val database = AccountingDb(sqlDriver)

    val serializer = JsonSerializer()


    val invoiceRepository: InvoiceRepository2 = SqlInvoiceRepository(database, serializer)

}