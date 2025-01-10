package net.codinux.accounting.domain.invoice.dataaccess

import assertk.assertThat
import assertk.assertions.isEqualByComparingTo
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import kotlinx.coroutines.test.runTest
import net.codinux.accounting.domain.persistence.AccountingPersistence
import net.codinux.accounting.domain.invoice.model.CreateEInvoiceOptions
import net.codinux.accounting.domain.invoice.model.CreateInvoiceSettings
import net.codinux.accounting.domain.invoice.model.ServiceDateOptions
import net.codinux.accounting.domain.serialization.InMemoryDataStorage
import net.codinux.accounting.domain.test.InvoiceAsserter
import net.codinux.accounting.domain.testdata.DataGenerator
import net.codinux.invoicing.model.EInvoiceXmlFormat
import kotlin.test.Test

class JsonInvoiceRepositoryTest {

    private val underTest = JsonInvoiceRepository(AccountingPersistence.serializer, InMemoryDataStorage())


    @Test
    fun json_SaveAndRetrieveCreateInvoiceSettings() = runTest {
        val settings = CreateInvoiceSettings(
            DataGenerator.createInvoice(),
            ServiceDateOptions.ServicePeriodMonth, EInvoiceXmlFormat.XRechnung, CreateEInvoiceOptions.CreateXmlAndPdf, false,
            "/path1", "/path2", "/path3"
        )


        underTest.saveCreateInvoiceSettings(settings)

        val result = underTest.loadCreateInvoiceSettings()


        assertThat(result).isNotNull()

        assertThat(result!!.selectedServiceDateOption).isEqualByComparingTo(settings.selectedServiceDateOption)
        assertThat(result.selectedEInvoiceXmlFormat).isEqualByComparingTo(settings.selectedEInvoiceXmlFormat)
        assertThat(result.selectedCreateEInvoiceOption).isEqualByComparingTo(settings.selectedCreateEInvoiceOption)
        assertThat(result.showGeneratedEInvoiceXml).isEqualTo(settings.showGeneratedEInvoiceXml)

        assertThat(result.lastXmlSaveDirectory).isEqualTo(settings.lastXmlSaveDirectory)
        assertThat(result.lastPdfSaveDirectory).isEqualTo(settings.lastPdfSaveDirectory)
        assertThat(result.lastOpenFileDirectory).isEqualTo(settings.lastOpenFileDirectory)

        InvoiceAsserter.assertInvoice(result.lastCreatedInvoice)
    }

}