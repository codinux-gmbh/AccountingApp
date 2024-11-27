package net.codinux.accounting.ui.extensions

import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.log.Log
import java.io.File

val PlatformFile.parent: String?
    get() {
        this.path?.let { path ->
            if (path.startsWith("content:") == false) {
                try {
                    return File(path).parent
                } catch (e: Throwable) {
                    Log.error(e) { "Could not get parent directory from PlatformFile ${this.path} / $this"}
                }
            }

            try {
                return path.substringBeforeLast('/')
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