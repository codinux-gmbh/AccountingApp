package net.codinux.accounting.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.codinux.accounting.domain.invoice.dataaccess.InvoiceRepository
import net.codinux.accounting.domain.invoice.service.EpcQrCodeGenerator
import net.codinux.accounting.domain.mail.service.MailService
import net.codinux.accounting.domain.persistence.AccountingSqlPersistence
import net.codinux.accounting.domain.ui.dataaccess.UiStateRepository
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.reader.EInvoiceReader
import kotlin.coroutines.CoroutineContext

actual class PlatformDependencies actual constructor(
    uiState: UiState,
    invoiceReader: EInvoiceReader
) {
    actual val fileHandler: PlatformFileHandler = PlatformFileHandler()

    actual val uiStateRepository: UiStateRepository = AccountingSqlPersistence.sqlUiStateRepository

    actual val invoiceRepository: InvoiceRepository = AccountingSqlPersistence.sqlInvoiceRepository

    actual val invoicePdfTemplateSettingsRepository = AccountingSqlPersistence.sqlInvoicePdfTemplateSettingsRepository

    actual val epcQrCodeGenerator: EpcQrCodeGenerator? = EpcQrCodeGenerator()

    actual val mailService: MailService? = null
}


actual val Dispatchers.IoOrDefault: CoroutineContext
    get() = Dispatchers.IO