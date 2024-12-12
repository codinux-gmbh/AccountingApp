package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.forms.HorizontalLabelledValue
import net.codinux.accounting.ui.composables.forms.Section
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.extensions.verticalScroll
import net.codinux.invoicing.model.*
import net.codinux.invoicing.model.codes.Currency
import org.jetbrains.compose.resources.stringResource


private val formatUtil = DI.formatUtil

@Composable
fun InvoiceView(invoice: Invoice) {

    SelectionContainer(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().verticalScroll()) {
            Section(Res.string.invoice_details) {
                HorizontalLabelledValue(Res.string.invoice_date, formatUtil.formatShortDate(invoice.details.invoiceDate))

                HorizontalLabelledValue(Res.string.invoice_number, invoice.details.invoiceNumber)
            }

            Section(Res.string.supplier) {
                PersonFields(invoice.supplier)
            }

            Section(Res.string.customer) {
                PersonFields(invoice.customer)
            }

            Section(Res.string.description_of_services) {
                // TODO: check if the service period is stated in eInvoice

                Text(stringResource(Res.string.delivered_goods_or_provided_services), Modifier.padding(top = 8.dp), fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)

                invoice.items.forEachIndexed { index, item ->
                    InvoiceItemView(index, item, invoice.details.currency)
                }

                invoice.totals?.let { TotalAmountsView(invoice.details.currency, it, true) }
            }

            invoice.supplier.bankDetails?.let { BankDetailsView(it, invoice.supplier) }
        }
    }

}

@Composable
private fun PersonFields(party: Party) {
    HorizontalLabelledValue(Res.string.name, party.name)

    HorizontalLabelledValue(Res.string.address, party.address)

    HorizontalLabelledValue(Res.string.city, "${party.postalCode} ${party.city}${party.country.alpha2Code}") // TODO: translate; // TODO: this can't be valid that all countries have an alpha-2 code

    HorizontalLabelledValue(Res.string.email, party.email)
    party.phone?.let { HorizontalLabelledValue(Res.string.phone, party.phone) }
    party.fax?.let { HorizontalLabelledValue(Res.string.fax, party.fax) }
    party.contactName?.let { HorizontalLabelledValue(Res.string.contact_name, party.contactName) }

    HorizontalLabelledValue(Res.string.vat_id_or_tax_number, party.vatId)
}

@Composable
private fun InvoiceItemView(zeroBasedItemIndex: Int, item: InvoiceItem, currency: Currency) {
    Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("${zeroBasedItemIndex + 1}.", Modifier.padding(start = 4.dp), textAlign = TextAlign.End)

        Text(item.name, Modifier.padding(start = 4.dp).weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)

        Text(formatUtil.formatQuantity(item.quantity), Modifier.width(32.dp).padding(start = 4.dp), textAlign = TextAlign.End, maxLines = 1)
        Text(item.unit, Modifier.width(32.dp).padding(start = 4.dp), maxLines = 1)
        Text("à", Modifier.padding(start = 4.dp))
        Text(formatUtil.formatAmountOfMoney(item.unitPrice, currency, true), Modifier.width(64.dp).padding(start = 4.dp), maxLines = 1)
        // Text(",")
        Text(formatUtil.formatPercentage(item.vatRate), Modifier.width(34.dp).padding(start = 4.dp), maxLines = 1)
    }
}

@Composable
private fun BankDetailsView(details: BankDetails, accountHolder: Party) {
    Section(Res.string.bank_details) {
        HorizontalLabelledValue(Res.string.account_holder, details.accountHolderName ?: accountHolder.name)

        HorizontalLabelledValue(Res.string.name_of_financial_institution, details.financialInstitutionName ?: "")

        HorizontalLabelledValue(Res.string.iban, details.accountNumber)

        HorizontalLabelledValue(Res.string.bic, details.bankCode)
    }
}