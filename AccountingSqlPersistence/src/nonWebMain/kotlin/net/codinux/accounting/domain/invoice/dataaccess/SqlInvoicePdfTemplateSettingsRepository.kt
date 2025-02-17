package net.codinux.accounting.domain.invoice.dataaccess

import net.codinux.accounting.domain.persistence.SqldelightMapper
import net.codinux.accounting.persistence.AccountingDb
import net.codinux.invoicing.model.Image
import net.codinux.invoicing.model.InvoiceLanguage
import net.codinux.invoicing.pdf.InvoicePdfTemplateSettings

class SqlInvoicePdfTemplateSettingsRepository(database: AccountingDb, private val mapper: SqldelightMapper)
    : InvoicePdfTemplateSettingsRepository {

    private val queries = database.invoicePdfTemplateSettingsQueries


    override fun loadInvoicePdfTemplateSettings(): InvoicePdfTemplateSettings? =
        queries.getInvoicePdfTemplateSettings { _, template, language, logoUrl, fontFamily, fontSize, textColor, lineItemColumnsToShow, headerColor, footerColor ->
            InvoicePdfTemplateSettings(
                language?.let { mapper.mapToEnum(it, InvoiceLanguage.entries) },
                logoUrl?.let { Image(it) }
            )
        }.executeAsOneOrNull()

    override suspend fun saveInvoicePdfTemplateSettings(settings: InvoicePdfTemplateSettings) {
        queries.upsertInvoicePdfTemplateSettings("Default", settings.language?.let { mapper.mapEnum(it) }, settings.logo?.imageUrl,
            null, null, null, null, null, null)
    }

}