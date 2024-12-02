package net.codinux.accounting.domain.invoice.service

import android.content.Context
import net.codinux.invoicing.pdf.PdfTextExtractor
import net.codinux.invoicing.pdf.PdfTextExtractorResult
import net.dankito.text.extraction.pdf.PdfBoxAndroidPdfTextExtractor
import java.io.File

class PdfBoxAndroidPdfTextExtractor(applicationContext: Context) : PdfTextExtractor {

    private val extractor = PdfBoxAndroidPdfTextExtractor(applicationContext)


    override fun extractTextFromPdf(pdfFile: File): PdfTextExtractorResult {
        val result = extractor.extractText(pdfFile)

        return PdfTextExtractorResult(result.text, result.error?.exception)
    }

}