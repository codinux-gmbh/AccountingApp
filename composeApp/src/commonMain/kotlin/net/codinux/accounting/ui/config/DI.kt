package net.codinux.accounting.ui.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import net.codinux.accounting.domain.bookkeeping.service.InvoiceService
import net.codinux.accounting.domain.mail.dataaccess.MailRepository
import net.codinux.accounting.domain.mail.service.MailService
import net.codinux.accounting.ui.service.FormatUtil
import net.codinux.accounting.ui.state.UiState

object DI {

    val uiState = UiState()


    val formatUtil = FormatUtil()

    val jsonMapper = ObjectMapper().apply {
        findAndRegisterModules()

        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }


    val mailService = MailService(MailRepository(jsonMapper))

}