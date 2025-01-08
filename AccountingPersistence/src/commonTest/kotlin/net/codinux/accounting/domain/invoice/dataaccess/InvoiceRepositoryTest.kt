package net.codinux.accounting.domain.invoice.dataaccess

import assertk.assertThat
import assertk.assertions.isEqualByComparingTo
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import kotlinx.coroutines.test.runTest
import net.codinux.accounting.domain.AccountingPersistence
import net.codinux.accounting.domain.invoice.model.CreateEInvoiceOptions
import net.codinux.accounting.domain.invoice.model.HistoricalInvoiceData
import net.codinux.accounting.domain.invoice.model.ServiceDateOptions
import net.codinux.accounting.domain.testdata.DataGenerator
import net.codinux.invoicing.model.EInvoiceXmlFormat
import kotlin.test.Test

class InvoiceRepositoryTest {

    private val underTest = AccountingPersistence.invoiceRepository


    @Test
    fun saveAndRetrieveCreateInvoiceSettings() = runTest {
        val settings = HistoricalInvoiceData(
            DataGenerator.createInvoice(),
            ServiceDateOptions.ServicePeriodMonth, EInvoiceXmlFormat.XRechnung, CreateEInvoiceOptions.CreateXmlAndPdf, false,
            "/path1", "/path2", "/path3"
        )


        underTest.saveHistoricalData(settings)

        val result = underTest.loadHistoricalData()


        assertThat(result).isNotNull()
        assertThat(result!!.lastCreatedInvoice).isEqualTo(settings.lastCreatedInvoice)

        assertThat(result.selectedServiceDateOption).isEqualByComparingTo(settings.selectedServiceDateOption)
        assertThat(result.selectedEInvoiceXmlFormat).isEqualByComparingTo(settings.selectedEInvoiceXmlFormat)
        assertThat(result.selectedCreateEInvoiceOption).isEqualByComparingTo(settings.selectedCreateEInvoiceOption)
        assertThat(result.showGeneratedEInvoiceXml).isEqualTo(settings.showGeneratedEInvoiceXml)

        assertThat(result.lastXmlSaveDirectory).isEqualTo(settings.lastXmlSaveDirectory)
        assertThat(result.lastPdfSaveDirectory).isEqualTo(settings.lastPdfSaveDirectory)
        assertThat(result.lastOpenFileDirectory).isEqualTo(settings.lastOpenFileDirectory)
    }

}