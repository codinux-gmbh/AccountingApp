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

    actual fun openFileInDefaultViewer(file: PlatformFile, fallbackMimeType: String?) {

    }

    actual fun saveCreatedInvoiceFile(invoice: Invoice, pdfBytes: ByteArray, xml: String, filename: String): PlatformFile {
        val fileManager = NSFileManager.defaultManager

        // TODO: write to user directory
        val xmlFileComponents = fileManager.temporaryDirectory.pathComponents?.plus(filename + ".xml")
            ?: throw IllegalStateException("Failed to get temporary directory")

        val xmlFile = NSURL.fileURLWithPathComponents(xmlFileComponents)
            ?: throw IllegalStateException("Failed to create XML file URL")

        writeBytesArrayToNsUrl(xml.encodeToByteArray(), xmlFile)


        val pdfFileComponents = fileManager.temporaryDirectory.pathComponents?.plus(filename + ".pdf")
            ?: throw IllegalStateException("Failed to get temporary directory")

        val pdfFile = NSURL.fileURLWithPathComponents(pdfFileComponents)
            ?: throw IllegalStateException("Failed to create PDF file URL")

        writeBytesArrayToNsUrl(pdfBytes, pdfFile)

        return PlatformFile(pdfFile)
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