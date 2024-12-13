package net.codinux.accounting.domain.invoice.service

import io.github.vinceglb.filekit.core.PlatformFile
import io.github.vinceglb.filekit.core.extension
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.common.model.localization.DisplayName
import net.codinux.accounting.domain.common.model.localization.PrioritizedDisplayNames
import net.codinux.accounting.domain.common.service.LocalizationService
import net.codinux.accounting.domain.invoice.dataaccess.InvoiceRepository
import net.codinux.accounting.domain.invoice.model.HistoricalInvoiceData
import net.codinux.accounting.platform.PlatformFileHandler
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.PlatformDependencies
import net.codinux.accounting.ui.state.UiState
import net.codinux.i18n.Region
import net.codinux.invoicing.creation.EInvoiceCreator
import net.codinux.invoicing.model.EInvoiceXmlFormat
import net.codinux.invoicing.model.Invoice
import net.codinux.invoicing.model.codes.Country
import net.codinux.invoicing.model.codes.Currency
import net.codinux.invoicing.reader.EInvoiceReader
import net.codinux.log.logger
import java.io.File
import java.time.format.DateTimeFormatter

class InvoiceService(
    private val uiState: UiState,
    private val creator: EInvoiceCreator = PlatformDependencies.invoiceCreator,
    private val reader: EInvoiceReader,
    private val localizationService: LocalizationService,
    private val repository: InvoiceRepository,
    private val fileHandler: PlatformFileHandler = PlatformDependencies.fileHandler,
    private val invoicesDirectory: File
) {

    companion object {
        private val InvoicingDateFilenameFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd")

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


    fun readEInvoice(file: PlatformFile): Invoice? =
        try {
            if (file.extension.lowercase() == "xml") {
                reader.extractFromXml(fileHandler.getInputStream(file)!!)
            } else {
                reader.extractFromPdf(fileHandler.getInputStream(file)!!)
            }
        } catch (e: Throwable) {
            log.error(e) { "Could not extract eInvoice data from file ${file.path}" }

            uiState.errorOccurred(ErroneousAction.ReadEInvoice, Res.string.error_message_could_not_read_e_invoice, e)

            null
        }


    // errors handled by InvoiceForm.createEInvoice()
    fun createEInvoiceXml(invoice: Invoice, format: EInvoiceXmlFormat): String = when (format) {
        EInvoiceXmlFormat.FacturX -> creator.createFacturXXml(invoice)
        EInvoiceXmlFormat.XRechnung -> creator.createXRechnungXml(invoice)
    }

    // errors handled by InvoiceForm.createEInvoice()
    suspend fun attachEInvoiceXmlToPdf(invoice: Invoice, format: EInvoiceXmlFormat, pdfFile: PlatformFile): String {
        val xml = createEInvoiceXml(invoice, format)
        val pdfBytes = pdfFile.readBytes() // as it's not possible to read and write from/to the same file at the same time, read PDF first (what PDFBox does anyway) before overwriting it

        creator.attachInvoiceXmlToPdf(xml, format, pdfBytes, fileHandler.getOutputStream(pdfFile)!!)

        return xml
    }

    // errors handled by InvoiceForm.createEInvoice()
    suspend fun createEInvoicePdf(invoice: Invoice, format: EInvoiceXmlFormat): Pair<String, PlatformFile> {
        val xml = createEInvoiceXml(invoice, format)

        val directory = File(invoicesDirectory, invoice.details.invoiceDate.year.toString()).also { it.mkdirs() }
        val filename = "${InvoicingDateFilenameFormat.format(invoice.details.invoiceDate)} ${invoice.details.invoiceNumber} ${invoice.customer.name}"
        val pdfFile = File(directory, filename + ".pdf")

        creator.createPdfWithAttachedXml(xml, format, pdfFile)

        File(directory, filename + ".xml").writeText(xml)

        return Pair(xml, fileHandler.fromPath(pdfFile.absolutePath))
    }

}