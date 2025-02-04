package net.codinux.accounting.domain.invoice.model

import io.github.vinceglb.filekit.core.PlatformFile

data class GeneratedInvoices(
    val xml: String,
    val xmlFile: PlatformFile?,
    val pdfBytes: ByteArray?,
    val pdfFile: PlatformFile?
)
