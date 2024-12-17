package net.codinux.accounting.ui

import net.codinux.accounting.platform.PlatformFileHandler
import net.codinux.invoicing.creation.EInvoiceCreator
import java.io.File

expect object PlatformDependencies {

    val applicationDataDirectory: File

    val fileHandler: PlatformFileHandler


    val invoiceCreator: EInvoiceCreator

}