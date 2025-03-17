package net.codinux.accounting.platform

import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.refTo
import net.codinux.invoicing.model.Invoice
import platform.Foundation.*

actual class PlatformFileHandler {

    actual fun fromPath(path: String): PlatformFile =
        PlatformFile(NSURL(fileURLWithPath = path))

    actual fun getRestorablePath(file: PlatformFile): String? =
        // it's not restorable. Also NSURL.startAccessingSecurityScopedResource() didn't help
        null


    actual fun openFileInDefaultViewer(file: PlatformFile, fallbackMimeType: String?) {

    }

    actual fun saveCreatedInvoiceFile(invoice: Invoice, fileContent: ByteArray, filename: String): PlatformFile {
        val fileManager = NSFileManager.defaultManager
        val directory = fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)[0] as? NSURL

        val fileComponents = directory?.pathComponents?.plus(filename)
            ?: throw IllegalStateException("Failed to get document directory")

        val file = NSURL.fileURLWithPathComponents(fileComponents)
            ?: throw IllegalStateException("Failed to create file URL")

        writeBytesArrayToNsUrl(fileContent, file)

        return PlatformFile(file)
    }

    actual fun savePdfWithAttachedXml(pdfFile: PlatformFile, pdfBytes: ByteArray) {
        writeBytesArrayToNsUrl(pdfBytes, pdfFile.nsUrl)
    }


    @OptIn(ExperimentalForeignApi::class)
    private fun writeBytesArrayToNsUrl(bytes: ByteArray?, nsUrl: NSURL) {
        val nsData = memScoped {
            if (bytes == null) return@memScoped NSData()

            NSData.dataWithBytes(
                bytes = bytes.refTo(0).getPointer(this),
                length = bytes.size.toULong()
            )
        }

        nsData.writeToURL(nsUrl, true)
    }

}