package net.codinux.accounting.platform

import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.invoicing.model.Invoice
import org.w3c.files.File

actual class PlatformFileHandler {

    actual fun fromPath(path: String): PlatformFile {
        // copied this code from FileKit.wasmJs .saveFile(), but don't know if it's correct

        // Create a byte array
//        val array = Uint8Array(bytes.size)
//        for (i in bytes.indices) {
//            array[i] = bytes[i]
//        }

        // Create a JS array
        val jsArray = JsArray<JsAny?>()
//        jsArray[0] = array

        // Create a blob
        val file = File(
            fileBits = jsArray,
            fileName = path,
        )

        return PlatformFile(file)
    }

    // TODO: file.path is always null in JS Browser (for security reasons). But what could be done:
    //   Create an object that contains file's bytes, display file name and when clicked using read file's bytes
    actual fun getRestorablePath(file: PlatformFile): String? = file.path


    actual fun openFileInDefaultViewer(file: PlatformFile, fallbackMimeType: String?) {
    }

    actual fun saveCreatedInvoiceFile(invoice: Invoice, fileContent: ByteArray, filename: String): PlatformFile {
        return fromPath(filename + ".pdf") // TODO:
    }

    actual fun savePdfWithAttachedXml(pdfFile: PlatformFile, pdfBytes: ByteArray) {
    }


}