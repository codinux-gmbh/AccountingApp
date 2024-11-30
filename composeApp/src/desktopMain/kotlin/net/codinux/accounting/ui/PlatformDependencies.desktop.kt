package net.codinux.accounting.ui

import net.codinux.accounting.platform.PlatformFileHandler
import net.codinux.invoicing.creation.EInvoiceCreator
import net.codinux.invoicing.pdf.*
import net.codinux.invoicing.reader.EInvoiceReader
import java.io.File

actual object PlatformDependencies {

    actual val applicationDataDirectory = File(System.getProperty("user.home"), ".accounting")

    actual val fileHandler = PlatformFileHandler()


    actual val pdfTextExtractor: PdfTextExtractor = PdfBoxPdfTextExtractor()


    actual val invoiceReader = EInvoiceReader()

    actual val invoiceCreator = EInvoiceCreator()

}