package net.codinux.accounting.domain.persistence

import net.codinux.accounting.domain.invoice.dataaccess.JsonInvoiceRepository
import net.codinux.accounting.domain.serialization.InMemoryDataStorage
import net.codinux.accounting.domain.serialization.JsonSerializer

object AccountingPersistence {

    val serializer = JsonSerializer()

    val jsonInvoiceRepository = JsonInvoiceRepository(serializer, InMemoryDataStorage())

}