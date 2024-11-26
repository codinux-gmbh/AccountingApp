package net.codinux.accounting.domain.invoice.service

import android.content.Context
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.cos.COSName
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDDocumentNameDictionary
import com.tom_roush.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode
import com.tom_roush.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification
import com.tom_roush.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile
import net.codinux.log.logger
import java.io.File
import java.io.InputStream
import java.io.OutputStream

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


    fun addFileAttachment(pdfFileInputStream: InputStream, attachmentName: String, xml: String, output: OutputStream) =
        addFileAttachment(pdfFileInputStream.readBytes(), attachmentName, xml, output)

    fun addFileAttachment(pdfFile: ByteArray, attachmentName: String, xml: String, output: OutputStream) {
        try {
            PDDocument.load(pdfFile).use { document ->
                val names = PDDocumentNameDictionary(document.documentCatalog)
                val embeddedFiles = names.embeddedFiles ?: PDEmbeddedFilesNameTreeNode()

                val fileMap = (embeddedFiles.names?.toMutableMap() ?: mutableMapOf())

                val cosStream = document.document.createCOSStream()
                cosStream.createOutputStream().use {
                    it.bufferedWriter().use { writer ->
                        writer.write(xml)
                    }
                }
                cosStream.setItem(COSName.TYPE, COSName.EMBEDDED_FILES)
                cosStream.setString(COSName.SUBTYPE, "application/xml")

                val fileSpec = PDComplexFileSpecification()
                fileSpec.file = attachmentName
                fileSpec.embeddedFile = PDEmbeddedFile(cosStream)

                fileMap.put(fileSpec.file, fileSpec)

                embeddedFiles.names = fileMap

                names.embeddedFiles = embeddedFiles
                document.documentCatalog.names = names

                document.save(output)
            }
        } catch (e: Throwable) {
            log.error(e) { "Could not add XML file attachments to PDF" }
        }
    }
}