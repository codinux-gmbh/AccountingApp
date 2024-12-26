package net.codinux.accounting.ui

import net.codinux.accounting.domain.invoice.dataaccess.InvoiceRepository
import net.codinux.accounting.domain.mail.service.MailService
import net.codinux.accounting.platform.PlatformFileHandler
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.reader.EInvoiceReader

expect class PlatformDependencies constructor(uiState: UiState, invoiceReader: EInvoiceReader) {

    val fileHandler: PlatformFileHandler

    val invoiceRepository: InvoiceRepository

    val mailService: MailService?

}