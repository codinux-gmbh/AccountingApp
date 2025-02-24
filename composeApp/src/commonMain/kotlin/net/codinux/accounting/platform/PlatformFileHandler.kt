package net.codinux.accounting.platform

import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.invoicing.model.Invoice

expect class PlatformFileHandler {

    fun fromPath(path: String): PlatformFile

    /**
     * On Android simply getting a file's path does not work, it's a kind of token for this file which
     * cannot be restored. To be able to retrieve a file again on next app start additional actions
     * need to be taken on Android, that's what's this method is for.
     */
    fun getRestorablePath(file: PlatformFile): String?


    fun openFileInDefaultViewer(file: PlatformFile, fallbackMimeType: String? = null)


    fun saveCreatedInvoiceFile(invoice: Invoice, fileContent: ByteArray, filename: String): PlatformFile

    fun savePdfWithAttachedXml(pdfFile: PlatformFile, pdfBytes: ByteArray)

}