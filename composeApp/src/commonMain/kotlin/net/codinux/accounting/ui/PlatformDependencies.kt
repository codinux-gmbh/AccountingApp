package net.codinux.accounting.ui

import net.codinux.accounting.platform.PlatformFileHandler
import net.codinux.invoicing.creation.EInvoiceCreator
import net.codinux.invoicing.pdf.PdfTextExtractor
import net.codinux.invoicing.reader.EInvoiceReader
import java.io.File

expect object PlatformDependencies {

    val applicationDataDirectory: File

    val fileHandler: PlatformFileHandler


    val pdfTextExtractor: PdfTextExtractor

    val invoiceReader: EInvoiceReader

    val invoiceCreator: EInvoiceCreator

}