package net.codinux.accounting.ui

import java.io.File

actual object InstanceCreator {

    actual val storageDir = File(File(System.getProperty("user.home"), ".accounting"), "storage").also {
        it.mkdirs()
    }

}