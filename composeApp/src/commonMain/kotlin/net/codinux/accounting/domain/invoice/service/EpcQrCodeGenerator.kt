package net.codinux.accounting.domain.invoice.service

import net.codinux.invoicing.model.BankDetails
import net.codinux.invoicing.model.Invoice

expect class EpcQrCodeGenerator {

    fun generateEpcQrCode(details: BankDetails, invoice: Invoice, accountHolderName: String, heightAndWidth: Int = 500): ByteArray?

}