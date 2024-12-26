package net.codinux.accounting.ui

import net.codinux.accounting.platform.PlatformFileHandler

actual object PlatformDependencies {

    private val applicationContext = AndroidContext.applicationContext

    actual val applicationDataDirectory = applicationContext.filesDir

    actual val fileHandler = PlatformFileHandler(applicationContext)

}