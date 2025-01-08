package net.codinux.accounting.domain.invoice.service

import net.codinux.banking.epcqrcode.EpcQrCodeConfig
import net.codinux.banking.epcqrcode.EpcQrCodeGenerator
import net.codinux.invoicing.model.BankDetails
import net.codinux.invoicing.model.Invoice
import net.codinux.log.logger

actual class EpcQrCodeGenerator {

    private val epcQrCodeGenerator = EpcQrCodeGenerator()

    private val log by logger()


    actual fun generateEpcQrCode(details: BankDetails, invoice: Invoice, accountHolderName: String, heightAndWidth: Int): ByteArray? =
        try {
            val amount = invoice.totals?.duePayableAmount?.toPlainString()
            val epcQrCode = epcQrCodeGenerator.generateEpcQrCode(EpcQrCodeConfig(accountHolderName, details.accountNumber, details.bankCode, amount, qrCodeHeightAndWidth = heightAndWidth))

            epcQrCode.bytes
        } catch (e: Throwable) {
            log.error(e) { "could not generate EPC QR Code for receiver = '$accountHolderName', IBAN = ${details.accountNumber}, amount = ${invoice.totals?.duePayableAmount}"}
            null
        }

}