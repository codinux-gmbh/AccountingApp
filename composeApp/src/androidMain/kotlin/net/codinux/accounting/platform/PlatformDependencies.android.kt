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
import net.codinux.accounting.domain.ui.dataaccess.UiStateRepository
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.email.EmailsFetcher
import net.codinux.invoicing.reader.EInvoiceReader
import net.codinux.kotlin.android.AndroidContext
import java.io.File
import kotlin.coroutines.CoroutineContext


actual val Dispatchers.IoOrDefault: CoroutineContext
    get() = Dispatchers.IO



actual class PlatformDependencies actual constructor(uiState: UiState, invoiceReader: EInvoiceReader) {

    private val jsonMapper = ObjectMapper().apply {
        findAndRegisterModules()

        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }


    private val applicationContext = AndroidContext.applicationContext

    private val applicationDataDirectory = applicationContext.filesDir

    private val invoicesDirectory = ensureDirectory(applicationDataDirectory, "invoices")

    private val databaseDirectory = ensureDirectory(applicationDataDirectory, "db")


    actual val fileHandler = PlatformFileHandler(applicationContext, invoicesDirectory)

    actual val uiStateRepository: UiStateRepository = AccountingSqlPersistence.sqlUiStateRepository

    actual val invoiceRepository: InvoiceRepository = AccountingSqlPersistence.sqlInvoiceRepository

    actual val invoicePdfTemplateSettingsRepository = AccountingSqlPersistence.sqlInvoicePdfTemplateSettingsRepository

    actual val epcQrCodeGenerator: EpcQrCodeGenerator? = EpcQrCodeGenerator()

    actual val mailService: MailService? = JvmMailService(uiState, EmailsFetcher(invoiceReader), MailRepository(jsonMapper, databaseDirectory))


    // TODO: move to common JVM and Android code
    private fun ensureDirectory(parentDir: File, directoryName: String): File = File(parentDir, directoryName).also {
        it.mkdirs()
    }

}