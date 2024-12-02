package net.codinux.accounting.domain.invoice.service

import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.invoice.dataaccess.InvoiceRepository
import net.codinux.accounting.domain.invoice.model.HistoricalInvoiceData
import net.codinux.accounting.platform.PlatformFileHandler
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.PlatformDependencies
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.creation.EInvoiceCreator
import net.codinux.invoicing.model.EInvoiceXmlFormat
import net.codinux.invoicing.model.Invoice
import net.codinux.log.logger
import java.io.File
import java.time.format.DateTimeFormatter

class InvoiceService(
    private val uiState: UiState,
    private val creator: EInvoiceCreator = PlatformDependencies.invoiceCreator,
    private val repository: InvoiceRepository,
    private val fileHandler: PlatformFileHandler = PlatformDependencies.fileHandler,
    private val invoicesDirectory: File
) {

    companion object {
        private val InvoicingDateFilenameFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    }


    private val log by logger()


    suspend fun init() {
        try {
            uiState.historicalInvoiceData.value = getHistoricalInvoiceData()
        } catch (e: Throwable) {
            log.error(e) { "Could not initialize persisted invoice data" }

            uiState.errorOccurred(ErroneousAction.LoadFromDatabase, Res.string.error_message_could_not_load_invoices, e)
        }
    }


    // errors handled by init()
    private suspend fun getHistoricalInvoiceData(): HistoricalInvoiceData {
        return repository.loadHistoricalData()
            ?: HistoricalInvoiceData()
    }

    // errors handled by InvoiceForm.createEInvoice()
    suspend fun saveHistoricalInvoiceData(data: HistoricalInvoiceData) {
        try {
            repository.saveHistoricalData(data)

            uiState.historicalInvoiceData.value = data
        } catch (e: Throwable) {
            log.error(e) { "Could not persist historical invoice data" }

            uiState.errorOccurred(ErroneousAction.SaveToDatabase, Res.string.error_message_could_not_persist_historical_invoice_data, e)
        }
    }


    // errors handled by InvoiceForm.createEInvoice()
    fun createEInvoiceXml(invoice: Invoice, format: EInvoiceXmlFormat): String = when (format) {
        EInvoiceXmlFormat.FacturX -> creator.createFacturXXml(invoice)
        EInvoiceXmlFormat.XRechnung -> creator.createXRechnungXml(invoice)
    }

    // errors handled by InvoiceForm.createEInvoice()
    suspend fun attachEInvoiceXmlToPdf(invoice: Invoice, format: EInvoiceXmlFormat, pdfFile: PlatformFile): String {
        val xml = createEInvoiceXml(invoice, format)
        val pdfBytes = pdfFile.readBytes() // as it's not possible to read and write from/to the same file at the same time, read PDF first (what PDFBox does anyway) before overwriting it

        creator.attachInvoiceXmlToPdf(xml, format, pdfBytes, fileHandler.getOutputStream(pdfFile)!!)

        return xml
    }

    // errors handled by InvoiceForm.createEInvoice()
    suspend fun createEInvoicePdf(invoice: Invoice, format: EInvoiceXmlFormat): Pair<String, PlatformFile> {
        val xml = createEInvoiceXml(invoice, format)

        val directory = File(invoicesDirectory, invoice.details.invoiceDate.year.toString()).also { it.mkdirs() }
        val filename = "${InvoicingDateFilenameFormat.format(invoice.details.invoiceDate)} ${invoice.details.invoiceNumber} ${invoice.customer.name}"
        val pdfFile = File(directory, filename + ".pdf")

        creator.createPdfWithAttachedXml(xml, format, pdfFile)

        File(directory, filename + ".xml").writeText(xml)

        return Pair(xml, fileHandler.fromPath(pdfFile.absolutePath))
    }

}