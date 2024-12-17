package net.codinux.accounting.ui

import net.codinux.accounting.platform.PlatformFileHandler
import net.codinux.invoicing.creation.EInvoicePdfCreator
import java.io.File

expect object PlatformDependencies {

    val applicationDataDirectory: File

    val fileHandler: PlatformFileHandler


    val pdfCreator: EInvoicePdfCreator?

}