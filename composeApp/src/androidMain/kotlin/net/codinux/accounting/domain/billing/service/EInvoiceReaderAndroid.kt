package net.codinux.accounting.domain.billing.service

import net.codinux.invoicing.model.Invoice
import net.codinux.invoicing.reader.EInvoiceReader
import java.io.InputStream

/**
 * EInvoiceReader uses PDFBox, which uses java.awt and therefore doesn't run on Android. So we
 * replace EInvoiceReader's calls to PDFBox by calls to PDFBox-Android to make it work on Android.
 */
class EInvoiceReaderAndroid(
    private val attachmentReaderAndWriter: PdfAttachmentReaderAndWriterAndroid
) : EInvoiceReader() {

    override fun extractXmlFromPdf(stream: InputStream): String {
        val xmlAttachments = attachmentReaderAndWriter.getXmlFileAttachments(stream)

        val xml = (xmlAttachments.firstOrNull { it.first.lowercase() in KnownEInvoiceXmlAttachmentNames }
            ?: xmlAttachments.firstOrNull())
            ?.second

        if (xml != null) {
            return xml
        }

        throw IllegalArgumentException("No XML attachment found in PDF file")
    }

    override fun extractFromPdf(stream: InputStream): Invoice =
        extractFromXml(extractXmlFromPdf(stream))

}