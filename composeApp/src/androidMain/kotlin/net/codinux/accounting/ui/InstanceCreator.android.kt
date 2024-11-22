package net.codinux.accounting.ui

import java.io.File

actual object InstanceCreator {

    actual val storageDir = File(AndroidContext.applicationContext.filesDir, "storage").also {
        it.mkdirs()
    }

}