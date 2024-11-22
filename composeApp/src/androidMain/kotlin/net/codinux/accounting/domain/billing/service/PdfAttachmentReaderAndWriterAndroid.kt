package net.codinux.accounting.domain.billing.service

import android.content.Context
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDDocumentNameDictionary
import net.codinux.log.logger
import java.io.File
import java.io.InputStream

class PdfAttachmentReaderAndWriterAndroid(
    applicationContext: Context
) {

    init {
        PDFBoxResourceLoader.init(applicationContext)
    }

    private val log by logger()


    fun getXmlFileAttachments(pdfFile: File) = getXmlFileAttachments(pdfFile.inputStream())

    fun getXmlFileAttachments(pdfFileInputStream: InputStream): List<Pair<String, String>> {
        try {
            PDDocument.load(pdfFileInputStream).use { document ->
                val names = PDDocumentNameDictionary(document.documentCatalog)
                val embeddedFiles = names.embeddedFiles
                val fileMap = (embeddedFiles.names ?: emptyMap())

                return fileMap.mapNotNull { (name, fileSpec) ->
                    if (name.lowercase().endsWith(".xml") || fileSpec.filename.lowercase().endsWith(".xml")) {
                        name to fileSpec.embeddedFile.cosObject.toTextString()
                    } else {
                        null
                    }
                }
            }
        } catch (e: Throwable) {
            log.error(e) { "Could not list XML file attachments of PDF" }
        }

        return emptyList()
    }
}