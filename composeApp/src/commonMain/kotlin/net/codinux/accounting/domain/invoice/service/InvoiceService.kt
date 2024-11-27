package net.codinux.accounting.domain.invoice.service

import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.accounting.platform.PlatformFileHandler
import net.codinux.accounting.ui.PlatformDependencies
import net.codinux.invoicing.creation.EInvoiceCreator
import net.codinux.invoicing.model.EInvoiceXmlFormat
import net.codinux.invoicing.model.Invoice
import java.io.File
import java.time.format.DateTimeFormatter

class InvoiceService(
    private val creator: EInvoiceCreator = PlatformDependencies.invoiceCreator,
    private val fileHandler: PlatformFileHandler = PlatformDependencies.fileHandler,
    private val invoicesDirectory: File
) {

    companion object {
        private val InvoicingDateFilenameFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    }

    fun createEInvoiceXml(invoice: Invoice, format: EInvoiceXmlFormat): String = when (format) {
        EInvoiceXmlFormat.FacturX -> creator.createFacturXXml(invoice)
        EInvoiceXmlFormat.XRechnung -> creator.createXRechnungXml(invoice)
    }

    suspend fun attachEInvoiceXmlToPdf(invoice: Invoice, format: EInvoiceXmlFormat, pdfFile: PlatformFile): String {
        val xml = createEInvoiceXml(invoice, format)
        val pdfBytes = pdfFile.readBytes() // as it's not possible to read and write from/to the same file at the same time, read PDF first (what PDFBox does anyway) before overwriting it

        creator.attachInvoiceXmlToPdf(xml, format, pdfBytes, fileHandler.getOutputStream(pdfFile)!!)

        return xml
    }

    suspend fun createEInvoicePdf(invoice: Invoice, format: EInvoiceXmlFormat): Pair<String, PlatformFile> {
        val xml = createEInvoiceXml(invoice, format)

        val directory = File(invoicesDirectory, invoice.invoicingDate.year.toString()).also { it.mkdirs() }
        val filename = "${InvoicingDateFilenameFormat.format(invoice.invoicingDate)} ${invoice.invoiceNumber} ${invoice.recipient.name}"
        val pdfFile = File(directory, filename + ".pdf")

        creator.createPdfWithAttachedXml(xml, format, pdfFile)

        File(directory, filename + ".xml").writeText(xml)

        return Pair(xml, fileHandler.fromPath(pdfFile.absolutePath))
    }

}