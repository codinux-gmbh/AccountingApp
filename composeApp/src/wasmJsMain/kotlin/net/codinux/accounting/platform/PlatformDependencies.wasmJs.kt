package net.codinux.accounting.platform

import kotlinx.coroutines.Dispatchers
import net.codinux.accounting.domain.invoice.dataaccess.*
import net.codinux.accounting.domain.invoice.service.EpcQrCodeGenerator
import net.codinux.accounting.domain.mail.service.MailService
import net.codinux.accounting.domain.persistence.AccountingPersistence
import net.codinux.accounting.domain.serialization.LocalStorageDataStorage
import net.codinux.accounting.domain.ui.dataaccess.JsonUiStateRepository
import net.codinux.accounting.domain.ui.dataaccess.UiStateRepository
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.reader.EInvoiceReader
import kotlin.coroutines.CoroutineContext

actual class PlatformDependencies actual constructor(uiState: UiState, invoiceReader: EInvoiceReader) {

    private val serializer = AccountingPersistence.serializer

    private val dataStorage = LocalStorageDataStorage()


    actual val fileHandler = PlatformFileHandler()

    actual val uiStateRepository: UiStateRepository = JsonUiStateRepository(serializer, dataStorage)

    actual val invoiceRepository: InvoiceRepository = JsonInvoiceRepository(serializer, dataStorage)

    actual val epcQrCodeGenerator: EpcQrCodeGenerator? = null

    actual val mailService: MailService? = null

}

actual val Dispatchers.IoOrDefault: CoroutineContext
    get() = Dispatchers.Default