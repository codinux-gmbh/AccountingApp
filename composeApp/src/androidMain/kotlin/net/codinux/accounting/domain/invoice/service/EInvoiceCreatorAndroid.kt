package net.codinux.accounting.domain.invoice.service

import net.codinux.invoicing.creation.EInvoiceCreator
import net.codinux.invoicing.model.EInvoiceXmlFormat
import java.io.OutputStream

class EInvoiceCreatorAndroid(
    private val attachmentReaderAndWriter: PdfAttachmentReaderAndWriterAndroid
) : EInvoiceCreator() {

    override fun attachInvoiceXmlToPdf(invoiceXml: String, format: EInvoiceXmlFormat, pdfFile: ByteArray, outputFile: OutputStream) {
        val attachmentName = getFilenameForFormat(format)

        outputFile.use { outputStream ->
            attachmentReaderAndWriter.addFileAttachment(pdfFile, attachmentName, invoiceXml, outputStream)
        }
    }

}