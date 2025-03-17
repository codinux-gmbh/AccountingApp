package net.codinux.accounting.platform

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.extensions.extension
import net.codinux.invoicing.model.Invoice
import net.codinux.log.logger
import java.io.File
import java.io.OutputStream

actual class PlatformFileHandler(
    private val applicationContext: Context,
    private val invoicesDirectory: File,
) {

    private val log by logger()


    actual fun fromPath(path: String) = PlatformFile(Uri.parse(path), applicationContext)

    actual fun getRestorablePath(file: PlatformFile): String? = file.uri.let { uri ->
        if (uri.scheme == "content") {
            applicationContext.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        uri.toString()
    }


    fun getOutputStream(file: PlatformFile): OutputStream? =
        applicationContext.contentResolver.openOutputStream(file.uri)


    actual fun openFileInDefaultViewer(file: PlatformFile, fallbackMimeType: String?) {
        try {
            val uri = if (file.uri.scheme?.lowercase() == "content") file.uri
            // applicationContext.packageName + ".fileprovider" = the authority defined in AndroidManifest.xml
            else FileProvider.getUriForFile(applicationContext, applicationContext.packageName + ".fileprovider", File(file.uri.toString()))

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, applicationContext.contentResolver.getType(uri) ?: fallbackMimeType)
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            applicationContext.startActivity(intent)
        } catch (e: Throwable) {
            log.error(e) { "Could not open file '${file.uri}" }

            if (e is ActivityNotFoundException || e.message?.startsWith("No Activity found to handle Intent") == true) {
                DI.uiState.errorOccurred(ErroneousAction.ShowEInvoiceInExternalViewer, Res.string.error_message_no_application_found_to_show_file_of_type, file.extension.uppercase())
            } else {
                // TODO: it's may not always the created invoice
                DI.uiState.errorOccurred(ErroneousAction.ShowEInvoiceInExternalViewer, Res.string.error_message_created_invoice_cannot_be_displayed, e)
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
        getOutputStream(pdfFile)?.use {
            it.write(pdfBytes)
        }
    }

}