package net.codinux.accounting.platform

import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.config.DI
import net.codinux.invoicing.model.Invoice
import net.codinux.log.logger
import java.awt.Desktop
import java.io.File
import java.io.OutputStream

actual class PlatformFileHandler(
    private val invoicesDirectory: File,
) {

    private val log by logger()


    actual fun fromPath(path: String) = PlatformFile(File(path))

    actual fun getRestorablePath(file: PlatformFile): String? = file.file.absolutePath


    fun getOutputStream(file: PlatformFile): OutputStream = file.file.outputStream()


    actual fun openFileInDefaultViewer(file: PlatformFile, fallbackMimeType: String?) {
        if (Desktop.isDesktopSupported()) {
            try {
                val desktop = Desktop.getDesktop()

                desktop.open(file.file)
            } catch (e: Throwable) {
                log.error(e) { "Could not open file '${file.file}" }

                // TODO: it's may not always the created invoice
                DI.uiState.errorOccurred(ErroneousAction.ShowEInvoice, Res.string.error_message_created_invoice_cannot_be_displayed, e)
            }
        }
    }

    actual fun saveCreatedInvoiceFile(invoice: Invoice, fileContent: ByteArray, filename: String): PlatformFile {

        val directory = File(invoicesDirectory, invoice.details.invoiceDate.year.toString()).also { it.mkdirs() }
        val invoiceFile = File(directory, filename)

        invoiceFile.writeBytes(fileContent)

        return this.fromPath(invoiceFile.absolutePath)
    }

    actual fun savePdfWithAttachedXml(pdfFile: PlatformFile, pdfBytes: ByteArray) {
        getOutputStream(pdfFile).use {
            it.write(pdfBytes)
        }
    }

}