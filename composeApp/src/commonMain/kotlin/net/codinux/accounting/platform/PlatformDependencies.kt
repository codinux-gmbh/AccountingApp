package net.codinux.accounting.platform

import kotlinx.coroutines.Dispatchers
import net.codinux.accounting.domain.invoice.dataaccess.InvoicePdfTemplateSettingsRepository
import net.codinux.accounting.domain.invoice.dataaccess.InvoiceRepository
import net.codinux.accounting.domain.invoice.service.EpcQrCodeGenerator
import net.codinux.accounting.domain.mail.service.MailService
import net.codinux.accounting.domain.ui.dataaccess.UiStateRepository
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.reader.EInvoiceReader
import kotlin.coroutines.CoroutineContext

expect class PlatformDependencies constructor(uiState: UiState, invoiceReader: EInvoiceReader) {

    val fileHandler: PlatformFileHandler

    val uiStateRepository: UiStateRepository

    val invoiceRepository: InvoiceRepository

    val invoicePdfTemplateSettingsRepository: InvoicePdfTemplateSettingsRepository

    val epcQrCodeGenerator: EpcQrCodeGenerator?

    val mailService: MailService?

}

expect val Dispatchers.IoOrDefault: CoroutineContext