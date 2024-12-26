package net.codinux.accounting.ui

import net.codinux.accounting.platform.PlatformFileHandler
import java.io.File

expect object PlatformDependencies {

    val applicationDataDirectory: File

    val fileHandler: PlatformFileHandler

}