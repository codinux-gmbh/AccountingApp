package net.codinux.accounting.platform

import io.github.vinceglb.filekit.core.PlatformFile
import java.io.InputStream
import java.io.OutputStream

expect class PlatformFileHandler {

    fun getInputStream(file: PlatformFile): InputStream?

    fun getOutputStream(file: PlatformFile): OutputStream?


    fun openFileInDefaultViewer(file: PlatformFile)

}