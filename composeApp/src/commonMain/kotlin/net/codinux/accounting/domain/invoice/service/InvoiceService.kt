package net.codinux.accounting.domain.invoice.service

import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.accounting.platform.PlatformFileHandler
import net.codinux.accounting.ui.PlatformDependencies
import net.codinux.invoicing.creation.EInvoiceCreator
import net.codinux.invoicing.model.EInvoiceXmlFormat
import net.codinux.invoicing.model.Invoice

class InvoiceService(
    private val creator: EInvoiceCreator = PlatformDependencies.invoiceCreator,
    private val fileHandler: PlatformFileHandler = PlatformDependencies.fileHandler
) {

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

    suspend fun createEInvoicePdf(invoice: Invoice, format: EInvoiceXmlFormat, outputFile: PlatformFile): String {
        val xml = createEInvoiceXml(invoice, format)

        creator.createPdfWithAttachedXml(xml, format, fileHandler.getOutputStream(outputFile)!!)

        return xml
    }

}