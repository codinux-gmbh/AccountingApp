package net.codinux.accounting.ui

import net.codinux.invoicing.reader.EInvoiceReader
import java.io.File

expect object PlatformDependencies {

    val storageDir: File

    val invoiceReader: EInvoiceReader

}