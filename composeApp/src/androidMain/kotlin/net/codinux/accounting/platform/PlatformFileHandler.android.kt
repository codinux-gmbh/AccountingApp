package net.codinux.accounting.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import io.github.vinceglb.filekit.core.PlatformFile
import java.io.InputStream
import java.io.OutputStream

actual class PlatformFileHandler(
    private val applicationContext: Context
) {

    actual fun fromPath(path: String) = PlatformFile(Uri.parse(path), applicationContext)


    actual fun getInputStream(file: PlatformFile): InputStream? =
        applicationContext.contentResolver.openInputStream(file.uri)

    actual fun getOutputStream(file: PlatformFile): OutputStream? =
        applicationContext.contentResolver.openOutputStream(file.uri)


    actual fun openFileInDefaultViewer(file: PlatformFile) {
        val uri = file.uri

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, applicationContext.contentResolver.getType(uri))
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        applicationContext.startActivity(intent)
    }

}