package net.codinux.accounting.domain.invoice.service

import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.common.model.localization.PrioritizedDisplayNames
import net.codinux.accounting.domain.common.service.LocalizationService
import net.codinux.accounting.domain.invoice.dataaccess.InvoiceRepository
import net.codinux.accounting.domain.invoice.model.CreateInvoiceSettings
import net.codinux.accounting.domain.invoice.model.ViewInvoiceSettings
import net.codinux.accounting.platform.PlatformFileHandler
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.extensions.parent
import net.codinux.accounting.ui.state.UiState
import net.codinux.i18n.Region
import net.codinux.invoicing.creation.*
import net.codinux.invoicing.model.*
import net.codinux.invoicing.model.codes.Country
import net.codinux.invoicing.model.codes.Currency
import net.codinux.invoicing.model.codes.UnitOfMeasure
import net.codinux.invoicing.reader.EInvoiceReader
import net.codinux.invoicing.reader.ReadEInvoiceFileResult
import net.codinux.invoicing.reader.extractFromFile
import net.codinux.log.logger

class InvoiceService(
    private val uiState: UiState,
    private val reader: EInvoiceReader,
    private val repository: InvoiceRepository,
    private val fileHandler: PlatformFileHandler,
    private val epcQrCodeGenerator: EpcQrCodeGenerator?,
    private val pdfCreator: EInvoicePdfCreator = EInvoicePdfCreator(),
    private val pdfAttacher: EInvoiceXmlToPdfAttacher = EInvoiceXmlToPdfAttacher(),
    private val xmlCreator: EInvoiceXmlCreator = EInvoiceXmlCreator(),
    private val localizationService: LocalizationService = LocalizationService()
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
            UnitOfMeasure.H87, UnitOfMeasure.NAR, UnitOfMeasure.C62, // piece, number of articles, unit
            UnitOfMeasure.PR, UnitOfMeasure.SET, // pair, set

            UnitOfMeasure.DAY, UnitOfMeasure.HUR, UnitOfMeasure.MIN, UnitOfMeasure.SEC,
            UnitOfMeasure.LTR, UnitOfMeasure.MLT, UnitOfMeasure.MTQ,
            UnitOfMeasure.MTR, UnitOfMeasure.CMT, UnitOfMeasure.MMT, UnitOfMeasure.MTK,
            UnitOfMeasure.GRM, UnitOfMeasure.KGM, UnitOfMeasure.TNE,
            UnitOfMeasure.AMP,
            UnitOfMeasure.KWT, UnitOfMeasure.MAW, UnitOfMeasure.A90,
            UnitOfMeasure.WHR, UnitOfMeasure.KWH, UnitOfMeasure.MWH, UnitOfMeasure.GWH,
            UnitOfMeasure.JOU, UnitOfMeasure.KJO, UnitOfMeasure._3B, UnitOfMeasure.GV,

            UnitOfMeasure.ZZ // mutually defined
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
        val all = localizationService.getAllUnitDisplayNames().sortedBy { it.displayName }
        val allByCode = all.associateBy { it.value.code }
        val preferredValues = PrioritizedUnitOfMeasure.map { allByCode[it.code]!! }
        val minorValues = all.toMutableList().apply { removeAll(preferredValues) }

        PrioritizedDisplayNames(all, preferredValues, minorValues)
    }

    private val log by logger()


    suspend fun init() {
        try {
            uiState.viewInvoiceSettings.value = getViewInvoiceSettings()

            uiState.createInvoiceSettings.value = getCreateInvoiceSettings()
        } catch (e: Throwable) {
            log.error(e) { "Could not initialize persisted InvoiceSettings" }

            uiState.errorOccurred(ErroneousAction.LoadFromDatabase, Res.string.error_message_could_not_load_invoices, e)
        }
    }


    fun getCountryDisplayNamesSorted() = sortedCountryDisplayNames

    fun getCurrencyDisplayNamesSorted() = sortedCurrencyDisplayNames

    fun getUnitOfMeasureDisplayNamesSorted() = sortedUnitOfMeasure


    // errors handled by init()
    private suspend fun getViewInvoiceSettings(): ViewInvoiceSettings {
        return repository.loadViewInvoiceSettings()
            ?: ViewInvoiceSettings()
    }

    suspend fun saveViewInvoiceSettings(settings: ViewInvoiceSettings) {
        try {
            repository.saveViewInvoiceSettings(settings)

            uiState.viewInvoiceSettings.value = settings
        } catch (e: Throwable) {
            log.error(e) { "Could not persist ViewInvoiceSettings" }

            // don't show an error message in this case to user, it's not important enough
//            uiState.errorOccurred(ErroneousAction.SaveToDatabase, Res.string.error_message_could_not_persist_create_invoice_settings, e)
        }
    }


    // errors handled by init()
    private suspend fun getCreateInvoiceSettings(): CreateInvoiceSettings {
        return repository.loadCreateInvoiceSettings()
            ?: CreateInvoiceSettings()
    }

    suspend fun saveCreateInvoiceSettings(settings: CreateInvoiceSettings) {
        try {
            repository.saveCreateInvoiceSettings(settings)

            uiState.createInvoiceSettings.value = settings
        } catch (e: Throwable) {
            log.error(e) { "Could not persist CreateInvoiceSettings" }

            uiState.errorOccurred(ErroneousAction.SaveToDatabase, Res.string.error_message_could_not_persist_create_invoice_settings, e)
        }
    }


    suspend fun readEInvoice(file: PlatformFile): ReadEInvoiceFileResult =
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
    suspend fun createEInvoicePdf(invoice: Invoice, format: EInvoiceXmlFormat, invoiceXmlCreated: ((String?) -> Unit)? = null): Triple<String, PlatformFile?, ByteArray?>? {
        val xml = createEInvoiceXml(invoice, format)
        invoiceXmlCreated?.invoke(xml)
        if (xml == null) {
            // TODO: show error message
            return null
        }

        val pdfBytes = pdfCreator.createFacturXPdf(xml, format)
        if (pdfBytes == null) {
            // TODO: show error message
            return Triple(xml, null, null)
        }

        val filename = "${invoice.details.invoiceDate.toDotSeparatedIsoDate()} ${invoice.details.invoiceNumber} ${invoice.customer.name}"

        return Triple(xml, fileHandler.saveCreatedInvoiceFile(invoice, pdfBytes, xml, filename), pdfBytes)
    }


    fun generateEpcQrCode(details: BankDetails, invoice: Invoice, accountHolderName: String, heightAndWidth: Int = 500): ByteArray? =
        epcQrCodeGenerator?.generateEpcQrCode(details, invoice, accountHolderName, heightAndWidth)

}