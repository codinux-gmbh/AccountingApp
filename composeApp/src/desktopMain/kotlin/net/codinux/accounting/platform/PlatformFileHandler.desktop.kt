package net.codinux.accounting.platform

import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.config.DI
import net.codinux.log.logger
import java.awt.Desktop
import java.io.File
import java.io.InputStream
import java.io.OutputStream

actual class PlatformFileHandler {

    private val log by logger()


    actual fun fromPath(path: String) = PlatformFile(File(path))


    actual fun getInputStream(file: PlatformFile): InputStream? = file.file.inputStream()

    actual fun getOutputStream(file: PlatformFile): OutputStream? = file.file.outputStream()


    actual fun openFileInDefaultViewer(file: PlatformFile) {
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

}