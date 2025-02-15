package net.codinux.accounting.domain.invoice.model

import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.invoicing.model.Pdf

data class GeneratedInvoices(
    val xml: String,
    val xmlFile: PlatformFile?,
    val pdf: Pdf?,
    val pdfFile: PlatformFile?
)
