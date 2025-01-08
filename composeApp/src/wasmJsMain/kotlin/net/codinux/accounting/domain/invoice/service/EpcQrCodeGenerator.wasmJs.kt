package net.codinux.accounting.domain.invoice.service

import net.codinux.invoicing.model.BankDetails
import net.codinux.invoicing.model.Invoice

actual class EpcQrCodeGenerator {

    actual fun generateEpcQrCode(details: BankDetails, invoice: Invoice, accountHolderName: String, heightAndWidth: Int): ByteArray? =
        null // TODO

}