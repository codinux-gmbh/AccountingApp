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
import net.codinux.invoicing.format.EInvoiceFormat
import kotlin.test.Test

class JsonInvoiceRepositoryTest {

    private val underTest = JsonInvoiceRepository(AccountingPersistence.serializer, InMemoryDataStorage())


    @Test
    fun saveAndRetrieveCreateInvoiceSettings() = runTest {
        val settings = CreateInvoiceSettings(
            DataGenerator.createInvoice(),
            true, true, true, true,
            ServiceDateOptions.ServicePeriodMonth, EInvoiceFormat.XRechnung, CreateEInvoiceOptions.CreateXmlAndPdf, false,
            "/path1", "/path2", "/path3"
        )


        underTest.saveCreateInvoiceSettings(settings)

        val result = underTest.loadCreateInvoiceSettings()


        assertThat(result).isNotNull()

        assertThat(result!!.showAllSupplierFields).isEqualTo(settings.showAllSupplierFields)
        assertThat(result.showAllCustomerFields).isEqualTo(settings.showAllCustomerFields)
        assertThat(result.showAllBankDetailsFields).isEqualTo(settings.showAllBankDetailsFields)

        assertThat(result.selectedServiceDateOption).isEqualByComparingTo(settings.selectedServiceDateOption)
        assertThat(result.selectedEInvoiceFormat).isEqualByComparingTo(settings.selectedEInvoiceFormat)
        assertThat(result.selectedCreateEInvoiceOption).isEqualByComparingTo(settings.selectedCreateEInvoiceOption)
        assertThat(result.showGeneratedEInvoiceXml).isEqualTo(settings.showGeneratedEInvoiceXml)

        assertThat(result.lastXmlSaveDirectory).isEqualTo(settings.lastXmlSaveDirectory)
        assertThat(result.lastPdfSaveDirectory).isEqualTo(settings.lastPdfSaveDirectory)
        assertThat(result.lastOpenPdfDirectory).isEqualTo(settings.lastOpenPdfDirectory)

        InvoiceAsserter.assertInvoice(result.lastCreatedInvoice)
    }

}