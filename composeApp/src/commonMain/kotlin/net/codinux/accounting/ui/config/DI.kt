package net.codinux.accounting.ui.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import net.codinux.accounting.domain.invoice.service.InvoiceService
import net.codinux.accounting.domain.mail.dataaccess.MailRepository
import net.codinux.accounting.domain.mail.service.MailService
import net.codinux.accounting.ui.PlatformDependencies
import net.codinux.accounting.ui.service.FormatUtil
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.email.EmailsFetcher
import java.io.File

object DI {

    val uiState = UiState()


    val formatUtil = FormatUtil()

    val jsonMapper = ObjectMapper().apply {
        findAndRegisterModules()

        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }


    val applicationDataDirectory = PlatformDependencies.applicationDataDirectory

    val dataDirectory = File(applicationDataDirectory, "data").also {
        it.mkdirs()
    }

    val fileHandler = PlatformDependencies.fileHandler

    private val invoiceReader = PlatformDependencies.invoiceReader

    val invoiceService = InvoiceService(PlatformDependencies.invoiceCreator, fileHandler)

    val mailService = MailService(uiState, EmailsFetcher(invoiceReader), MailRepository(jsonMapper, dataDirectory))


    suspend fun init() {
        mailService.init()
    }

}