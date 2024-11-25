package net.codinux.accounting.ui.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import net.codinux.accounting.domain.invoice.service.InvoiceService
import net.codinux.accounting.domain.mail.dataaccess.MailRepository
import net.codinux.accounting.domain.mail.service.MailService
import net.codinux.accounting.ui.PlatformDependencies
import net.codinux.accounting.ui.service.FormatUtil
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.mail.MailReader

object DI {

    val uiState = UiState()


    val formatUtil = FormatUtil()

    val jsonMapper = ObjectMapper().apply {
        findAndRegisterModules()

        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }


    private val invoiceReader = PlatformDependencies.invoiceReader

    val invoiceService = InvoiceService()

    val mailService = MailService(MailReader(invoiceReader), MailRepository(jsonMapper))


    suspend fun init() {
        mailService.init(uiState)
    }

}