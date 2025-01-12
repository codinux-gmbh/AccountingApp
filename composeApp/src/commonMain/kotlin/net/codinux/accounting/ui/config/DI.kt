package net.codinux.accounting.ui.config

import net.codinux.accounting.domain.invoice.service.CalculationService
import net.codinux.accounting.domain.invoice.service.InvoiceService
import net.codinux.accounting.domain.ui.service.UiService
import net.codinux.accounting.platform.PlatformDependencies
import net.codinux.accounting.ui.service.FormatUtil
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.reader.EInvoiceReader

object DI {

    val uiState = UiState()


    val formatUtil = FormatUtil()


    private val invoiceReader = EInvoiceReader()

    private val platformDependencies = PlatformDependencies(uiState, invoiceReader)


    val fileHandler = platformDependencies.fileHandler


    val uiService = UiService(uiState, platformDependencies.uiStateRepository)


    val calculationService = CalculationService()

    val invoiceService = InvoiceService(uiState, invoiceReader, platformDependencies.invoiceRepository, fileHandler, platformDependencies.epcQrCodeGenerator)


    val mailService = platformDependencies.mailService


    suspend fun init() {
        uiService.init()

        invoiceService.init()

        mailService?.init()
    }

}