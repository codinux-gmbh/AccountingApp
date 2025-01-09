package net.codinux.accounting.platform

import kotlinx.coroutines.Dispatchers
import net.codinux.accounting.domain.invoice.dataaccess.*
import net.codinux.accounting.domain.invoice.service.EpcQrCodeGenerator
import net.codinux.accounting.domain.mail.service.MailService
import net.codinux.accounting.domain.persistence.AccountingPersistence
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.reader.EInvoiceReader
import kotlin.coroutines.CoroutineContext

actual class PlatformDependencies actual constructor(uiState: UiState, invoiceReader: EInvoiceReader) {

    actual val fileHandler = PlatformFileHandler()

    actual val invoiceRepository: InvoiceRepository = AccountingPersistence.jsonInvoiceRepository

    actual val epcQrCodeGenerator: EpcQrCodeGenerator? = null

    actual val mailService: MailService? = null

}

actual val Dispatchers.IoOrDefault: CoroutineContext
    get() = Dispatchers.Default