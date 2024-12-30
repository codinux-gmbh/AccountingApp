package net.codinux.accounting.domain.invoice.service

import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.common.model.localization.DisplayName
import net.codinux.accounting.domain.common.model.localization.PrioritizedDisplayNames
import net.codinux.accounting.domain.common.service.LocalizationService
import net.codinux.accounting.domain.invoice.dataaccess.InvoiceRepository
import net.codinux.accounting.domain.invoice.model.HistoricalInvoiceData
import net.codinux.accounting.platform.PlatformFileHandler
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.extensions.parent
import net.codinux.accounting.ui.state.UiState
import net.codinux.i18n.Region
import net.codinux.invoicing.creation.*
import net.codinux.invoicing.model.EInvoiceXmlFormat
import net.codinux.invoicing.model.Invoice
import net.codinux.invoicing.model.codes.Country
import net.codinux.invoicing.model.codes.Currency
import net.codinux.invoicing.model.codes.UnitOfMeasure
import net.codinux.invoicing.reader.EInvoiceReader
import net.codinux.invoicing.reader.FileEInvoiceExtractionResult
import net.codinux.invoicing.reader.extractFromFile
import net.codinux.log.logger

class InvoiceService(
    private val uiState: UiState,
    private val reader: EInvoiceReader,
    private val repository: InvoiceRepository,
    private val fileHandler: PlatformFileHandler,
    private val pdfCreator: EInvoicePdfCreator = EInvoicePdfCreator(),
    private val pdfAttacher: EInvoiceXmlToPdfAttacher = EInvoiceXmlToPdfAttacher(),
    private val xmlCreator: EInvoiceXmlCreator = EInvoiceXmlCreator(),
    private val localizationService: LocalizationService = LocalizationService(),
) {

    companion object {
        private val EuropeanCountries = (Region.WesternEurope.contains + Region.NorthernEurope.contains
                + Region.EasternEurope.contains + Region.SouthernEurope.contains).toSet()

        private val PrioritizedCountries = listOf(
            *Country.entries.filter { it.alpha2Code in EuropeanCountries }.toTypedArray(),
            Country.UnitedStates, Country.Canada, Country.Australia, Country.Russia, Country.India, Country.China, Country.Japan
        )

        private val PrioritizedCurrencies = listOf(
            *Currency.entries.filter { it.isFrequentlyUsedValue }.toTypedArray(),
            Currency.RussianRuble, Currency.YuanRenminbi, Currency.Yen
        )

        private val PrioritizedUnitOfMeasure = listOf(
            UnitOfMeasure.NAR, UnitOfMeasure.C62, UnitOfMeasure.PR, UnitOfMeasure.SET,
            UnitOfMeasure.DAY, UnitOfMeasure.HUR, UnitOfMeasure.MIN, UnitOfMeasure.SEC,
            UnitOfMeasure.LTR, UnitOfMeasure.MLT, UnitOfMeasure.MTQ,
            UnitOfMeasure.MTR, UnitOfMeasure.CMT, UnitOfMeasure.MMT, UnitOfMeasure.MTK,
            UnitOfMeasure.GRM, UnitOfMeasure.KGM, UnitOfMeasure.TNE,
            UnitOfMeasure.AMP,
            UnitOfMeasure.KWT, UnitOfMeasure.MAW, UnitOfMeasure.A90,
            UnitOfMeasure.WHR, UnitOfMeasure.KWH, UnitOfMeasure.MWH, UnitOfMeasure.GWH,
            UnitOfMeasure.JOU, UnitOfMeasure.KJO, UnitOfMeasure._3B, UnitOfMeasure.GV,
            UnitOfMeasure.ZZ
        )
    }


    private val sortedCountryDisplayNames: PrioritizedDisplayNames<Country> by lazy {
        val all = localizationService.getAllCountryDisplayNames().sortedBy { it.displayName }
        val preferredValues = all.filter { it.value in PrioritizedCountries }.sortedBy { it.displayName }
        val minorValues = all.filter { it.value !in PrioritizedCountries }.sortedBy { it.displayName }

        PrioritizedDisplayNames(all, preferredValues, minorValues)
    }

    private val sortedCurrencyDisplayNames: PrioritizedDisplayNames<Currency> by lazy {
        val all = localizationService.getAllCurrencyDisplayNames().sortedBy { it.displayName }
        val preferredValues = all.filter { it.value in PrioritizedCurrencies }.sortedBy { it.displayName }
        val minorValues = all.filter { it.value !in PrioritizedCurrencies }.sortedBy { it.displayName }

        PrioritizedDisplayNames(all, preferredValues, minorValues)
    }

    private val sortedUnitOfMeasure: PrioritizedDisplayNames<UnitOfMeasure> by lazy {
        val all = UnitOfMeasure.entries.map { DisplayName(it, it.englishName) }.sortedBy { it.value.englishName } // TODO: translate
        val allByCode = all.associateBy { it.value.code }
        val preferredValues = PrioritizedUnitOfMeasure.map { allByCode[it.code]!! }
        val minorValues = all.toMutableList().apply { removeAll(preferredValues) }

        PrioritizedDisplayNames(all, preferredValues, minorValues)
    }

    private val log by logger()


    suspend fun init() {
        try {
            uiState.historicalInvoiceData.value = getHistoricalInvoiceData()
        } catch (e: Throwable) {
            log.error(e) { "Could not initialize persisted invoice data" }

            uiState.errorOccurred(ErroneousAction.LoadFromDatabase, Res.string.error_message_could_not_load_invoices, e)
        }
    }


    fun getCountryDisplayNamesSorted() = sortedCountryDisplayNames

    fun getCurrencyDisplayNamesSorted() = sortedCurrencyDisplayNames

    fun getUnitOfMeasureDisplayNamesSorted() = sortedUnitOfMeasure


    // errors handled by init()
    private suspend fun getHistoricalInvoiceData(): HistoricalInvoiceData {
        return repository.loadHistoricalData()
            ?: HistoricalInvoiceData()
    }

    // errors handled by InvoiceForm.createEInvoice()
    suspend fun saveHistoricalInvoiceData(data: HistoricalInvoiceData) {
        try {
            repository.saveHistoricalData(data)

            uiState.historicalInvoiceData.value = data
        } catch (e: Throwable) {
            log.error(e) { "Could not persist historical invoice data" }

            uiState.errorOccurred(ErroneousAction.SaveToDatabase, Res.string.error_message_could_not_persist_historical_invoice_data, e)
        }
    }


    suspend fun readEInvoice(file: PlatformFile): FileEInvoiceExtractionResult =
        reader.extractFromFile(file.readBytes(), file.name, file.parent, null)


    // errors handled by InvoiceForm.createEInvoice()
    suspend fun createEInvoiceXml(invoice: Invoice, format: EInvoiceXmlFormat): String? =
        xmlCreator.createInvoiceXml(invoice, format)

    // errors handled by InvoiceForm.createEInvoice()
    suspend fun attachEInvoiceXmlToPdf(invoice: Invoice, format: EInvoiceXmlFormat, pdfFile: PlatformFile): String? {
        val xml = createEInvoiceXml(invoice, format)

        if (xml != null) {
            val pdfBytes = pdfFile.readBytes() // as it's not possible to read and write from/to the same file at the same time, read PDF first (what PDFBox does anyway) before overwriting it

            val resultPdfBytes = pdfAttacher.attachInvoiceXmlToPdf(invoice, pdfBytes, format)
            if (resultPdfBytes != null) {
                fileHandler.savePdfWithAttachedXml(pdfFile, resultPdfBytes)
            }

            // TODO: detach XML from created PDF and return that one
        } else {
            // TODO: show error message
        }

        return xml
    }

    // errors handled by InvoiceForm.createEInvoice()
    suspend fun createEInvoicePdf(invoice: Invoice, format: EInvoiceXmlFormat): Triple<String, PlatformFile?, ByteArray?>? {
        val xml = createEInvoiceXml(invoice, format)
        if (xml == null) {
            // TODO: show error message
            return null
        }

        val pdfBytes = pdfCreator.createFacturXPdf(xml, format)
        if (pdfBytes == null) {
            // TODO: show error message
            return Triple(xml, null, null)
        }

        return Triple(xml, fileHandler.saveCreatedInvoiceFile(invoice, pdfBytes, xml), pdfBytes)
    }

}