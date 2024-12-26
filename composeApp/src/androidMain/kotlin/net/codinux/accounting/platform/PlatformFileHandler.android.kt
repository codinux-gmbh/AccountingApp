package net.codinux.accounting.platform

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.accounting.domain.common.extensions.extension
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.config.DI
import net.codinux.log.logger
import java.io.InputStream
import java.io.OutputStream

actual class PlatformFileHandler(
    private val applicationContext: Context
) {

    private val log by logger()


    actual fun fromPath(path: String) = PlatformFile(Uri.parse(path), applicationContext)


    actual fun getInputStream(file: PlatformFile): InputStream? =
        applicationContext.contentResolver.openInputStream(file.uri)

    actual fun getOutputStream(file: PlatformFile): OutputStream? =
        applicationContext.contentResolver.openOutputStream(file.uri)


    actual fun openFileInDefaultViewer(file: PlatformFile) {
        try {
            val uri = file.uri

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, applicationContext.contentResolver.getType(uri))
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            applicationContext.startActivity(intent)
        } catch (e: Throwable) {
            log.error(e) { "Could not open file '${file.uri}" }

            if (e is ActivityNotFoundException || e.message?.startsWith("No Activity found to handle Intent") == true) {
                DI.uiState.errorOccurred(ErroneousAction.ShowEInvoice, Res.string.error_message_no_application_found_to_show_file_of_type, null, file.extension.uppercase())
            } else {
                // TODO: it's may not always the created invoice
                DI.uiState.errorOccurred(ErroneousAction.ShowEInvoice, Res.string.error_message_created_invoice_cannot_be_displayed, e)
            }
        }
    }

}