package net.codinux.accounting.domain.invoice.dataaccess

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.codinux.accounting.domain.invoice.model.HistoricalInvoiceData
import net.codinux.log.logger
import java.io.File

class InvoiceRepository(
    private val jsonMapper: ObjectMapper,
    dataDirectory: File
) {

    private val historicalDataFile = File(dataDirectory, "invoiceHistoricalData.json")

    private val log by logger()


    /**
     * Does not handle errors, [net.codinux.accounting.domain.invoice.service.InvoiceService] does this for us
     */
    suspend fun loadHistoricalData(): HistoricalInvoiceData? =
        if (historicalDataFile.exists()) {
            jsonMapper.readValue<HistoricalInvoiceData>(historicalDataFile)
        } else { // historical data have not been persisted yet
            null
        }

    suspend fun saveHistoricalData(data: HistoricalInvoiceData) {
        val json = jsonMapper.writeValueAsBytes(data)
        if (json.isNotEmpty()) {
            jsonMapper.writeValue(historicalDataFile, data)
        } else {
            log.error { "Could not map HistoricalInvoiceData to JSON" }
        }
    }

}