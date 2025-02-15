package net.codinux.accounting.domain.invoice.service

import androidx.compose.runtime.Composable
import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.common.model.localization.DisplayName
import net.codinux.accounting.domain.common.model.localization.PrioritizedDisplayNames
import net.codinux.accounting.domain.common.service.LocalizationService
import net.codinux.accounting.domain.invoice.dataaccess.InvoiceRepository
import net.codinux.accounting.domain.invoice.model.CreateInvoiceSettings
import net.codinux.accounting.domain.invoice.model.GeneratedInvoices
import net.codinux.accounting.domain.invoice.model.ViewInvoiceSettings
import net.codinux.accounting.platform.PlatformFileHandler
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.extensions.parent
import net.codinux.accounting.ui.state.UiState
import net.codinux.i18n.Region
import net.codinux.invoicing.creation.*
import net.codinux.invoicing.format.EInvoiceFormat
import net.codinux.invoicing.model.*
import net.codinux.invoicing.model.codes.Country
import net.codinux.invoicing.model.codes.Currency
import net.codinux.invoicing.model.codes.UnitOfMeasure
import net.codinux.invoicing.model.dto.SerializableException
import net.codinux.invoicing.reader.EInvoiceReader
import net.codinux.invoicing.reader.ReadEInvoiceFileResult
import net.codinux.invoicing.reader.extractFromFile
import net.codinux.invoicing.validation.*
import net.codinux.log.logger
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

class InvoiceService(
    private val uiState: UiState,
    private val reader: EInvoiceReader,
    private val repository: InvoiceRepository,
    private val fileHandler: PlatformFileHandler,
    private val epcQrCodeGenerator: EpcQrCodeGenerator?,
    private val pdfCreator: EInvoicePdfCreator = EInvoicePdfCreator(),
    private val pdfAttacher: EInvoiceXmlToPdfAttacher = EInvoiceXmlToPdfAttacher(),
    private val xmlCreator: EInvoiceXmlCreator = EInvoiceXmlCreator(),
    private val xmlValidator: EInvoiceXmlValidator = EInvoiceXmlValidator(),
    private val pdfValidator: EInvoicePdfValidator = EInvoicePdfValidator(),
    private val localizationService: LocalizationService = LocalizationService()
) {

    companion object {
        private val SmallerEuropeanCountries = setOf(
            Country.AlandIslands, Country.Andorra, Country.FaroeIslands, Country.Gibraltar,
            Country.Guernsey, Country.IsleOfMan, Country.Jersey, Country.Monaco,
            Country.SanMarino, Country.SvalbardAndJanMayen, Country.VaticanCity
        )

        private val EuropeanCountries = (Region.WesternEurope.contains + Region.NorthernEurope.contains
                + Region.EasternEurope.contains + Region.SouthernEurope.contains).toMutableSet().apply {
                    removeAll(SmallerEuropeanCountries.map { it.alpha2Code }.toSet())
        }

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

            UnitOfMeasure.DAY, UnitOfMeasure.HUR, UnitOfMeasure.MIN, // day, hour, minute
            UnitOfMeasure.LTR, UnitOfMeasure.MLT, // liter, milliliter
            UnitOfMeasure.MTR, UnitOfMeasure.CMT, UnitOfMeasure.MMT,  // meter, centimeter, millimeter
            UnitOfMeasure.MTK, UnitOfMeasure.MTQ, // square meter, cubic meter
            UnitOfMeasure.GRM, UnitOfMeasure.KGM, UnitOfMeasure.TNE, // gram, kilogram, metric tons
            UnitOfMeasure.AMP, // ampere
            UnitOfMeasure.WTT, UnitOfMeasure.KWT, // watt, kilowatt,
            UnitOfMeasure.WHR, UnitOfMeasure.KWH, // watt hour, kilowatt hour,
            UnitOfMeasure.JOU, UnitOfMeasure.KJO, // joule, kilojoule

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

    private lateinit var sortedUnitOfMeasure: PrioritizedDisplayNames<UnitOfMeasure>

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

    @Composable
    fun getUnitOfMeasureDisplayNamesSorted(): PrioritizedDisplayNames<UnitOfMeasure> {
        if (this::sortedUnitOfMeasure.isInitialized) {
            return sortedUnitOfMeasure
        }

        val all = localizationService.getAllUnitDisplayNames()
            .map {
                if (it.stringResourceForUntranslatedDisplayName != null) DisplayName(it.value, stringResource(it.stringResourceForUntranslatedDisplayName), it.shortName)
                else DisplayName(it.value, it.displayName, it.shortName)
            }
            .sortedBy { it.displayName }
        val allByCode = all.associateBy { it.value.code }
        val preferredValues = PrioritizedUnitOfMeasure.map { allByCode[it.code]!! }
        val minorValues = all.toMutableList().apply { removeAll(preferredValues) }

        val displayNames = PrioritizedDisplayNames(preferredValues, preferredValues, minorValues)
        this.sortedUnitOfMeasure = displayNames

        return displayNames
    }


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
    suspend fun createEInvoiceXml(invoice: Invoice, format: EInvoiceFormat): Pair<String?, PlatformFile?> {
        val result = xmlCreator.createInvoiceXml(invoice, format)

        return result.value?.let {
            val xml = it
            val xmlFile = saveCreatedInvoiceFile(invoice, xml.encodeToByteArray(), "xml")
            xml to xmlFile
        } ?: run {
            showCouldNotCreateInvoiceError(result.error, Res.string.error_message_could_not_create_invoice_pdf)
            null to null
        }
    }

    // errors handled by InvoiceForm.createEInvoice()
    suspend fun attachEInvoiceXmlToPdf(invoice: Invoice, format: EInvoiceFormat, pdfFile: PlatformFile): Pair<String?, PlatformFile?> {
        val (xml, xmlFile) = createEInvoiceXml(invoice, format)

        if (xml != null) {
            val pdfBytes = pdfFile.readBytes() // as it's not possible to read and write from/to the same file at the same time, read PDF first (what PDFBox does anyway) before overwriting it

            val attachResult = pdfAttacher.attachInvoiceXmlToPdf(invoice, pdfBytes, EInvoiceXmlFormat.valueOf(format.name))
            val resultPdfBytes = attachResult.value
            if (resultPdfBytes != null) {
                fileHandler.savePdfWithAttachedXml(pdfFile, resultPdfBytes)
            } else {
                showCouldNotCreateInvoiceError(attachResult.error, Res.string.error_message_could_not_attach_invoice_xml_to_pdf)
            }

            // TODO: detach XML from created PDF and return that one
        }

        return xml to xmlFile
    }

    // errors handled by InvoiceForm.createEInvoice()
    suspend fun createEInvoicePdf(invoice: Invoice, format: EInvoiceFormat, invoiceLanguage: InvoiceLanguage = InvoiceLanguage.English,
                                  invoiceLogoUrl: String? = null, invoiceXmlCreated: ((String?) -> Unit)? = null): GeneratedInvoices? {
        val xmlResult = createEInvoiceXml(invoice, format)
        val xml = xmlResult.first
        invoiceXmlCreated?.invoke(xml)
        if (xml == null) {
            return null
        }

        val pdfResult = pdfCreator.createFacturXPdf(xml, InvoicePdfConfig(EInvoiceXmlFormat.valueOf(format.name), invoiceLanguage, invoiceLogoUrl))
        val pdf = pdfResult.value
        if (pdf == null) {
            showCouldNotCreateInvoiceError(pdfResult.error, Res.string.error_message_could_not_create_invoice_pdf)
            return GeneratedInvoices(xml, xmlResult.second, null, null)
        }

        return GeneratedInvoices(xml, xmlResult.second, pdf, saveCreatedInvoiceFile(invoice, pdf.bytes, "pdf"))
    }

    private fun saveCreatedInvoiceFile(invoice: Invoice, fileContent: ByteArray, type: String): PlatformFile? =
        try {
            val filename = invoice.shortDescription + "." + type

            fileHandler.saveCreatedInvoiceFile(invoice, fileContent, filename)
        } catch (e: Throwable) {
            log.error(e) { "Could not save invoice '$invoice' $type file" }
            showCouldNotCreateInvoiceError(e, Res.string.error_message_could_not_save_invoice_file)
            null
        }

    fun showCouldNotCreateInvoiceError(error: Throwable?, message: StringResource? = null) {
        log.error(error) { "Could not create or save eInvoice" }

        uiState.errorOccurred(ErroneousAction.CreateInvoice, message ?: Res.string.error_message_could_not_create_invoice, error)
    }

    fun showCouldNotCreateInvoiceError(error: SerializableException?, message: StringResource? = null) {
        log.error { "Could not create or save eInvoice: $error" }

        uiState.errorOccurred(ErroneousAction.CreateInvoice, message ?: Res.string.error_message_could_not_create_invoice, error)
    }


    suspend fun validateInvoiceXml(invoiceXml: String): Result<InvoiceXmlValidationResult> =
        xmlValidator.validateEInvoiceXml(invoiceXml)

    suspend fun validateInvoicePdf(pdfBytes: ByteArray): Result<PdfValidationResult> =
        pdfValidator.validateEInvoicePdf(pdfBytes)


    fun generateEpcQrCode(details: BankDetails, invoice: Invoice, accountHolderName: String, heightAndWidth: Int = 500): ByteArray? =
        epcQrCodeGenerator?.generateEpcQrCode(details, invoice, accountHolderName, heightAndWidth)

}