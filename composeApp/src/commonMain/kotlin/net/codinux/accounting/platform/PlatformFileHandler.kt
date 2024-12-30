package net.codinux.accounting.platform

import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.invoicing.model.Invoice

expect class PlatformFileHandler {

    fun fromPath(path: String): PlatformFile


    fun openFileInDefaultViewer(file: PlatformFile, fallbackMimeType: String? = null)


    fun saveCreatedInvoiceFile(invoice: Invoice, pdfBytes: ByteArray, xml: String, filename: String): PlatformFile

    fun savePdfWithAttachedXml(pdfFile: PlatformFile, pdfBytes: ByteArray)

}