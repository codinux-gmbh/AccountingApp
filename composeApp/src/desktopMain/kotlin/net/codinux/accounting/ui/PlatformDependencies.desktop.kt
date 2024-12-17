package net.codinux.accounting.ui

import net.codinux.accounting.platform.PlatformFileHandler
import net.codinux.invoicing.creation.EInvoiceCreator
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

actual object PlatformDependencies {

    actual val applicationDataDirectory = determineDataDirectory()

    actual val fileHandler = PlatformFileHandler()


    actual val invoiceCreator = EInvoiceCreator()



    private fun determineDataDirectory(): File {
        val currentDir = Path(System.getProperty("user.dir"))

        // if the current directory is writable, use that one (the default for development)
        val dataDir = if (Files.isWritable(currentDir)) { // couldn't believe it, but java.io.File returned folder is writable for "C:\\Program Files\\"
            File(currentDir.absolutePathString(), "data")
        } else { // otherwise use .accounting dir in user's home dir (the default for releases)
            File(determineOsDependentUserDataDir(), ".accounting")
        }

        return dataDir.also { it.mkdirs() }
    }

    private fun determineOsDependentUserDataDir(): File {
        val userHomeString = System.getProperty("user.home")
        val userHome = File(userHomeString)
        val windowsLocalAppDataDir = System.getenv("LOCALAPPDATA")?.takeUnless { it.isBlank() }

        return if (windowsLocalAppDataDir != null) {
            File(windowsLocalAppDataDir)
        } else if (userHomeString.startsWith("/")) {
            val osName = System.getProperty("os.name")
            if (osName.contains("mac", true) || osName.contains("darwin", true)) { // macOS
                File(userHome, "Library/Application Support")
            } else if (osName.contains("nux")) { // Linux
                val localShareDirectory = File(userHome, ".local/share")
                val configDir = File(userHome, ".config")

                if (localShareDirectory.exists()) {
                    localShareDirectory
                } else if (configDir.exists()) {
                    configDir
                } else {
                    userHome
                }
            } else { // unknown
                userHome
            }
        } else if (userHomeString.length > 3 && userHomeString[1] == ':' && userHomeString[2] == '\\') { // Windows, but LOCALAPPDATA is not set
            userHome // File(userHome, "AppData\Local")
        } else {
            userHome
        }
    }
}