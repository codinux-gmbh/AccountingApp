package net.codinux.accounting.domain.invoice.service

import net.codinux.invoicing.reader.EInvoiceReader

/**
 * EInvoiceReader uses PDFBox, which uses java.awt and therefore doesn't run on Android. So we
 * replace EInvoiceReader's calls to PDFBox by calls to PDFBox-Android to make it work on Android.
 */
class EInvoiceReaderAndroid(
    attachmentReaderAndWriter: PdfAttachmentReaderAndWriterAndroid
) : EInvoiceReader(attachmentReaderAndWriter)