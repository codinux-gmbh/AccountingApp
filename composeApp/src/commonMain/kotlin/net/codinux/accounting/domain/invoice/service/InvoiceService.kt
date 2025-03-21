package net.codinux.accounting.domain.invoice.service

import androidx.compose.runtime.Composable
import io.github.vinceglb.filekit.core.PlatformFile
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.common.model.localization.DisplayName
import net.codinux.accounting.domain.common.model.localization.PrioritizedDisplayNames
import net.codinux.accounting.domain.common.service.LocalizationService
import net.codinux.accounting.domain.invoice.dataaccess.InvoicePdfTemplateSettingsRepository
import net.codinux.accounting.domain.invoice.dataaccess.InvoiceRepository
import net.codinux.accounting.domain.invoice.model.*
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
import net.codinux.invoicing.pdf.InvoicePdfSettings
import net.codinux.invoicing.pdf.InvoicePdfTemplateSettings
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
    private val invoicePdfTemplateSettingsRepository: InvoicePdfTemplateSettingsRepository,
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

            uiState.recentlyViewedInvoices.value = getRecentlyViewedInvoices()


            uiState.createInvoiceSettings.value = getCreateInvoiceSettings()

            uiState.invoicePdfTemplateSettings.value = getInvoicePdfTemplateSettings()
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
    private fun getRecentlyViewedInvoices() = repository.loadRecentlyViewedInvoices()


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


    // errors handled by init()
    private fun getInvoicePdfTemplateSettings(): InvoicePdfTemplateSettings {
        return invoicePdfTemplateSettingsRepository.loadInvoicePdfTemplateSettings()
            ?: InvoicePdfTemplateSettings()
    }

    suspend fun saveInvoicePdfTemplateSettings(settings: InvoicePdfTemplateSettings) {
        try {
            invoicePdfTemplateSettingsRepository.saveInvoicePdfTemplateSettings(settings)

            uiState.invoicePdfTemplateSettings.value = settings
        } catch (e: Throwable) {
            log.error(e) { "Could not persist InvoicePdfTemplateSettings" }

            uiState.errorOccurred(ErroneousAction.SaveToDatabase, Res.string.error_message_could_not_persist_invoice_pdf_template_settings, e)
        }
    }


    suspend fun readEInvoice(file: PlatformFile): ReadEInvoiceFileResult =
        reader.extractFromFile(file.readBytes(), file.name, file.parent, null)


    // errors handled by InvoiceForm.createEInvoice()
    suspend fun createEInvoiceXml(invoice: Invoice, format: EInvoiceFormat): GeneratedInvoices? {
        val result = xmlCreator.createInvoiceXml(invoice, format)
        val xml = result.value

        return if (xml == null) {
            showCouldNotCreateInvoiceError(result.error, Res.string.error_message_could_not_create_invoice_pdf)
            null
        } else {
            val xmlFile = saveCreatedInvoiceFile(invoice, xml.encodeToByteArray(), "xml")
            GeneratedInvoices(xml, xmlFile, null, null)
        }
    }

    // errors handled by InvoiceForm.createEInvoice()
    suspend fun attachEInvoiceXmlToPdf(invoice: Invoice, format: EInvoiceFormat, pdfFile: PlatformFile, invoiceXmlCreated: ((GeneratedInvoices) -> Unit)? = null): GeneratedInvoices? {
        val createXmlResult = createEInvoiceXml(invoice, format)

        if (createXmlResult == null) {
            return createXmlResult
        }


        invoiceXmlCreated?.invoke(createXmlResult)

        val pdfBytes = pdfFile.readBytes()

        val attachResult = pdfAttacher.attachInvoiceXmlToPdf(invoice, pdfBytes, EInvoiceXmlFormat.valueOf(format.name))
        val resultPdfBytes = attachResult.value

        return if (resultPdfBytes != null) {
            val savedPdf = saveCreatedInvoiceFile(invoice, resultPdfBytes, "pdf")
            GeneratedInvoices(createXmlResult.xml, createXmlResult.xmlFile, Pdf(resultPdfBytes), savedPdf)
        } else {
            showCouldNotCreateInvoiceError(attachResult.error, Res.string.error_message_could_not_attach_invoice_xml_to_pdf)
            createXmlResult
        }
    }

    // errors handled by InvoiceForm.createEInvoice()
    suspend fun createEInvoicePdf(invoice: Invoice, format: EInvoiceFormat, templateSettings: InvoicePdfTemplateSettings?, invoiceXmlCreated: ((GeneratedInvoices) -> Unit)? = null): GeneratedInvoices? {
        val xmlResult = createEInvoiceXml(invoice, format)
        val xml = xmlResult?.xml
        if (xml == null) {
            return null
        }

        invoiceXmlCreated?.invoke(xmlResult)

        val pdfResult = pdfCreator.createFacturXPdf(xml, InvoicePdfSettings(EInvoiceXmlFormat.valueOf(format.name), templateSettings))
        val pdf = pdfResult.value
        if (pdf == null) {
            showCouldNotCreateInvoiceError(pdfResult.error, Res.string.error_message_could_not_create_invoice_pdf)
            return GeneratedInvoices(xml, xmlResult.xmlFile, null, null)
        }

        return GeneratedInvoices(xml, xmlResult.xmlFile, pdf, saveCreatedInvoiceFile(invoice, pdf.bytes, "pdf"))
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


    fun addRecentlyViewedInvoice(invoiceFilePath: String?, selectedInvoice: ReadEInvoiceFileResult) {
        invoiceFilePath?.let {
            try {
                repository.addRecentlyViewedInvoice(RecentlyViewedInvoice(invoiceFilePath, selectedInvoice.invoice))

                uiState.recentlyViewedInvoices.value = getRecentlyViewedInvoices()
            } catch (e: Throwable) {
                log.error(e) { "Could not add RecentlyViewedInvoice" }
            }
        }
    }

}