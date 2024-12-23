package net.codinux.accounting.ui.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import net.codinux.accounting.domain.invoice.dataaccess.InvoiceRepository
import net.codinux.accounting.domain.invoice.service.CalculationService
import net.codinux.accounting.domain.invoice.service.InvoiceService
import net.codinux.accounting.domain.mail.dataaccess.MailRepository
import net.codinux.accounting.domain.mail.service.MailService
import net.codinux.accounting.ui.PlatformDependencies
import net.codinux.accounting.ui.service.FormatUtil
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.email.EmailsFetcher
import net.codinux.invoicing.reader.EInvoiceReader
import java.io.File

object DI {

    val uiState = UiState()


    val formatUtil = FormatUtil()

    private val jsonMapper = ObjectMapper().apply {
        findAndRegisterModules()

        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }


    private val applicationDataDirectory = PlatformDependencies.applicationDataDirectory

    private val databaseDirectory = ensureDirectory(applicationDataDirectory, "db")

    private val invoicesDirectory = ensureDirectory(applicationDataDirectory, "invoices")

    val fileHandler = PlatformDependencies.fileHandler


    private val invoiceReader = EInvoiceReader()

    val calculationService = CalculationService()

    val invoiceService = InvoiceService(uiState, invoiceReader, InvoiceRepository(jsonMapper, databaseDirectory),
        fileHandler, invoicesDirectory)


    val mailService = MailService(uiState, EmailsFetcher(invoiceReader), MailRepository(jsonMapper, databaseDirectory))


    suspend fun init() {
        invoiceService.init()

        mailService.init()
    }

    private fun ensureDirectory(parentDir: File, directoryName: String): File = File(parentDir, directoryName).also {
        it.mkdirs()
    }

}