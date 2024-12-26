package net.codinux.accounting.ui

import kotlinx.coroutines.Dispatchers
import net.codinux.accounting.domain.invoice.dataaccess.InvoiceRepository
import net.codinux.accounting.domain.mail.service.MailService
import net.codinux.accounting.platform.PlatformFileHandler
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.reader.EInvoiceReader
import kotlin.coroutines.CoroutineContext

actual class PlatformDependencies actual constructor(uiState: UiState, invoiceReader: EInvoiceReader) {

    actual val fileHandler = PlatformFileHandler()

    actual val invoiceRepository = InvoiceRepository()

    actual val mailService: MailService? = null

}

actual val Dispatchers.IoOrDefault: CoroutineContext
    get() = Dispatchers.Default