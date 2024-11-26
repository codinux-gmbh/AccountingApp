package net.codinux.accounting.ui

import net.codinux.accounting.platform.PlatformFileHandler
import net.codinux.invoicing.creation.EInvoiceCreator
import net.codinux.invoicing.reader.EInvoiceReader
import java.io.File

actual object PlatformDependencies {

    actual val storageDir = File(File(System.getProperty("user.home"), ".accounting"), "data").also {
        it.mkdirs()
    }

    actual val fileHandler = PlatformFileHandler()


    actual val invoiceReader = EInvoiceReader()

    actual val invoiceCreator = EInvoiceCreator()

}