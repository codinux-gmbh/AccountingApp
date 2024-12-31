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

val PlatformFile.parentDirAndFilename: String
    get() {
        val path = this.path

        if (path != null) {
            val separator = getSeparator()
            val lastIndex = path.lastIndexOfOrNull(separator)

            if (lastIndex != null) {
                val index = this.path?.lastIndexOfOrNull(separator, lastIndex - 1)

                return if (index != null) {
                    path.substring(index + 1)
                } else {
                    path
                }
            }
        }

        return this.name
    }

fun PlatformFile.getLastPathSegmentsOfMaxLength(maxLength: Int): String {
    val path = this.path

    return if (path == null) {
        this.parentDirAndFilename
    } else {
        val separator = this.getSeparator()
        val separatorIndices = path.mapIndexedNotNull { index, char -> if (char == separator) index else null }
        val index = separatorIndices.firstOrNull { path.length - it < maxLength }

        if (index != null) {
            path.substring(index + 1)
        } else {
            this.parentDirAndFilename
        }
    }
}

fun PlatformFile.getSeparator(): Char =
    if (this.path?.contains('\\') == true && this.path?.contains('/') == false) { // Windows
        '\\'
    } else { // all other
        '/'
    }