package net.codinux.accounting.platform

import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.log.logger
import java.awt.Desktop
import java.io.InputStream
import java.io.OutputStream

actual class PlatformFileHandler {

    private val log by logger()


    actual fun getInputStream(file: PlatformFile): InputStream? = file.file.inputStream()

    actual fun getOutputStream(file: PlatformFile): OutputStream? = file.file.outputStream()


    actual fun openFileInDefaultViewer(file: PlatformFile) {
        if (Desktop.isDesktopSupported()) {
            try {
                val desktop = Desktop.getDesktop()

                desktop.open(file.file)
            } catch (e: Throwable) {
                log.error(e) { "Could not open file '${file.file}" }
                // TODO: show error to user
            }
        }
    }

}