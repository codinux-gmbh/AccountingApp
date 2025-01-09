package net.codinux.accounting.platform

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import net.codinux.accounting.domain.invoice.dataaccess.InvoiceRepository
import net.codinux.accounting.domain.invoice.service.EpcQrCodeGenerator
import net.codinux.accounting.domain.mail.dataaccess.MailRepository
import net.codinux.accounting.domain.mail.service.JvmMailService
import net.codinux.accounting.domain.mail.service.MailService
import net.codinux.accounting.domain.persistence.AccountingSqlPersistence
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.email.EmailsFetcher
import net.codinux.invoicing.reader.EInvoiceReader
import java.io.File
import java.nio.file.Files
import kotlin.coroutines.CoroutineContext
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString


actual val Dispatchers.IoOrDefault: CoroutineContext
    get() = Dispatchers.IO


actual class PlatformDependencies actual constructor(uiState: UiState, invoiceReader: EInvoiceReader){

    private val jsonMapper = ObjectMapper().apply {
        findAndRegisterModules()

        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }


    private val applicationDataDirectory = determineDataDirectory()

    private val invoicesDirectory = ensureDirectory(applicationDataDirectory, "invoices")

    private val databaseDirectory = ensureDirectory(applicationDataDirectory, "db")

    actual val fileHandler = PlatformFileHandler(invoicesDirectory)

    actual val invoiceRepository: InvoiceRepository = AccountingSqlPersistence.sqlInvoiceRepository

    actual val epcQrCodeGenerator: EpcQrCodeGenerator? = EpcQrCodeGenerator()

    actual val mailService: MailService? = JvmMailService(uiState, EmailsFetcher(invoiceReader), MailRepository(jsonMapper, databaseDirectory))


    // TODO: move to common JVM and Android code
    private fun ensureDirectory(parentDir: File, directoryName: String): File = File(parentDir, directoryName).also {
        it.mkdirs()
    }



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