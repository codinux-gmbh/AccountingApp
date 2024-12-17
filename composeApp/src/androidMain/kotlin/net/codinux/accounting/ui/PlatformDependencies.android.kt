package net.codinux.accounting.ui

import net.codinux.accounting.domain.invoice.service.*
import net.codinux.accounting.platform.PlatformFileHandler
import net.codinux.invoicing.creation.EInvoiceCreator

actual object PlatformDependencies {

    private val applicationContext = AndroidContext.applicationContext

    actual val applicationDataDirectory = applicationContext.filesDir

    actual val fileHandler = PlatformFileHandler(applicationContext)


    private val attachmentReaderAndWriter = PdfAttachmentReaderAndWriterAndroid(applicationContext)

    actual val invoiceCreator: EInvoiceCreator = EInvoiceCreatorAndroid(attachmentReaderAndWriter)

}