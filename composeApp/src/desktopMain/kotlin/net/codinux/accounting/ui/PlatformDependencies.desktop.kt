package net.codinux.accounting.ui

import net.codinux.invoicing.reader.EInvoiceReader
import java.io.File

actual object PlatformDependencies {

    actual val storageDir = File(File(System.getProperty("user.home"), ".accounting"), "storage").also {
        it.mkdirs()
    }

    actual val invoiceReader = EInvoiceReader()

}