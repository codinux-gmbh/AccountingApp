package net.codinux.accounting.ui

import net.codinux.accounting.domain.invoice.service.EInvoiceCreatorAndroid
import net.codinux.accounting.domain.invoice.service.EInvoiceReaderAndroid
import net.codinux.accounting.domain.invoice.service.PdfAttachmentReaderAndWriterAndroid
import net.codinux.accounting.platform.PlatformFileHandler
import net.codinux.invoicing.creation.EInvoiceCreator
import net.codinux.invoicing.reader.EInvoiceReader

actual object PlatformDependencies {

    private val applicationContext = AndroidContext.applicationContext

    actual val applicationDataDirectory = applicationContext.filesDir

    actual val fileHandler = PlatformFileHandler(applicationContext)


    private val attachmentReaderAndWriter = PdfAttachmentReaderAndWriterAndroid(applicationContext)

    actual val invoiceReader: EInvoiceReader = EInvoiceReaderAndroid(attachmentReaderAndWriter)

    actual val invoiceCreator: EInvoiceCreator = EInvoiceCreatorAndroid(attachmentReaderAndWriter)

}