package net.codinux.accounting.ui

import net.codinux.accounting.domain.invoice.service.EInvoiceReaderAndroid
import net.codinux.accounting.domain.invoice.service.PdfAttachmentReaderAndWriterAndroid
import net.codinux.invoicing.reader.EInvoiceReader
import java.io.File

actual object PlatformDependencies {

    actual val storageDir = File(AndroidContext.applicationContext.filesDir, "data").also {
        it.mkdirs()
    }

    private val attachmentReaderAndWriter = PdfAttachmentReaderAndWriterAndroid(AndroidContext.applicationContext)

    actual val invoiceReader: EInvoiceReader = EInvoiceReaderAndroid(attachmentReaderAndWriter)

}