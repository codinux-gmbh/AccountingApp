package net.codinux.accounting.ui.extensions

import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.i18n.lastIndexOfOrNull
import net.codinux.log.Log
//import java.io.File

val PlatformFile.parent: String?
    get() {
        this.path?.let { path ->
//            if (path.startsWith("content:") == false) {
//                try {
//                    return File(path).parent
//                } catch (e: Throwable) {
//                    Log.error(e) { "Could not get parent directory from PlatformFile ${this.path} / $this"}
//                }
//            }

            try {
                return path.substringBeforeLast(this.getSeparator())
            }  catch (e: Throwable) {
                Log.error(e) { "Could not get parent directory from PlatformFile ${this.path} / $this"}
            }
        }

        return null
    }

val PlatformFile.parentDirName: String?
    get() {
        try {
            return this.parent?.substringAfterLast('/')
        }  catch (e: Throwable) {
            Log.error(e) { "Could not get parent directory name from PlatformFile ${this.path} / $this"}
        }

        return null
    }

val PlatformFile.parentDirAndFilename: String?
    get() {
        val separator = getSeparator()
        val lastIndex = this.path?.lastIndexOfOrNull(separator)

        return if (lastIndex != null) {
            val index = this.path?.lastIndexOfOrNull(separator, lastIndex - 1)
            if (index != null) {
                this.path?.substring(index)
            } else {
                this.path
            }
        } else {
            this.path
        }
    }

fun PlatformFile.getSeparator(): Char =
    if (this.path?.contains('\\') == true && this.path?.contains('/') == false) { // Windows
        '\\'
    } else { // all other
        '/'
    }