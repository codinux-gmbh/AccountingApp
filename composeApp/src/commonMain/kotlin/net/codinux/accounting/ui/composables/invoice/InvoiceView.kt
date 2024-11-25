package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.forms.HorizontalLabelledValue
import net.codinux.accounting.ui.composables.forms.RoundedCornersCard
import net.codinux.accounting.ui.composables.forms.SectionHeader
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.config.Style
import net.codinux.invoicing.model.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.math.BigDecimal


private val VerticalRowPadding = 2.dp

private val VerticalSectionPadding = 12.dp

private val formatUtil = DI.formatUtil

@Composable
fun InvoiceView(invoice: Invoice) {

    SelectionContainer(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth()) {
            Section(Res.string.invoice_details) {
                HorizontalLabelledValue(Res.string.invoice_date, formatUtil.formatShortDate(invoice.invoicingDate))

                HorizontalLabelledValue(Res.string.invoice_number, invoice.invoiceNumber)
            }

            Section(Res.string.issuer) {
                PersonFields(invoice.sender)
            }

            Section(Res.string.recipient) {
                PersonFields(invoice.recipient)
            }

            Section(Res.string.description_of_services) {
                // TODO: check if the service period is stated in eInvoice

                Text(stringResource(Res.string.delivered_goods_or_provided_services), Modifier.padding(top = 8.dp), fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)

                invoice.items.forEachIndexed { index, item ->
                    InvoiceItemView(index, item)
                }

                invoice.totalAmounts?.let { TotalAmountsView(it) }
            }

            invoice.sender.bankDetails?.let { BankDetailsView(it, invoice.sender) }
        }
    }

}

@Composable
private fun Section(titleResource: StringResource, content: @Composable () -> Unit) {
    RoundedCornersCard(Modifier.fillMaxWidth().padding(top = VerticalSectionPadding)) {
        Column(Modifier.padding(all = Style.FormCardPadding).padding(vertical = VerticalRowPadding)) {
            SectionHeader(stringResource(titleResource), false)

            content()
        }
    }
}

@Composable
private fun PersonFields(party: Party) {
    HorizontalLabelledValue(Res.string.name, party.name)

    HorizontalLabelledValue(Res.string.street, party.street)

    HorizontalLabelledValue(Res.string.city, "${party.postalCode} ${party.city}${party.countryIsoCode?.let { ", $it" } ?: ""}")

    HorizontalLabelledValue(Res.string.email, party.email)
    party.phone?.let { HorizontalLabelledValue(Res.string.phone, party.phone) }
    party.fax?.let { HorizontalLabelledValue(Res.string.fax, party.fax) }
    party.contactName?.let { HorizontalLabelledValue(Res.string.contact_name, party.contactName) }

    HorizontalLabelledValue(Res.string.vat_id_or_tax_number, party.vatId)
}

@Composable
private fun InvoiceItemView(zeroBasedItemIndex: Int, item: InvoiceItem) {
    Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("${zeroBasedItemIndex + 1}.", Modifier.padding(start = 4.dp), textAlign = TextAlign.End)

        Text(item.name, Modifier.padding(start = 4.dp).weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)

        Text(formatUtil.formatQuantity(item.quantity), Modifier.width(32.dp).padding(start = 4.dp), textAlign = TextAlign.End, maxLines = 1)
        Text(item.unit, Modifier.width(32.dp).padding(start = 4.dp), maxLines = 1)
        Text("à", Modifier.padding(start = 4.dp))
        Text(formatUtil.formatAmountOfMoney(item.unitPrice, true), Modifier.width(64.dp).padding(start = 4.dp), maxLines = 1)
        // Text(",")
        Text(formatUtil.formatPercentage(item.vatRate), Modifier.width(34.dp).padding(start = 4.dp), maxLines = 1)
    }
}

@Composable
private fun TotalAmountsView(amounts: TotalAmounts) {
    Row(Modifier.fillMaxWidth().padding(top = 6.dp), Arrangement.End, Alignment.CenterVertically) {
        Spacer(Modifier.weight(1f))
        Divider(Modifier.width(185.dp), Colors.ItemDividerColor)
    }

    AmountRow(Res.string.net_amount, amounts.lineTotalAmount)
    AmountRow(Res.string.vat_amount, amounts.taxTotalAmount)

    Row(Modifier.fillMaxWidth().padding(top = 6.dp), Arrangement.End, Alignment.CenterVertically) {
        Spacer(Modifier.weight(1f))
        Divider(Modifier.width(160.dp), Color.Black, 1.5.dp)
    }

    AmountRow(Res.string.total_amount, amounts.duePayableAmount)
}

@Composable
private fun AmountRow(label: StringResource, amount: BigDecimal) {
    Row(Modifier.fillMaxWidth().padding(top = 6.dp), Arrangement.End, Alignment.CenterVertically) {
        Spacer(Modifier.weight(1f))

        Text(stringResource(label), Modifier.width(70.dp), Colors.FormLabelTextColor, Style.LabelledValueFontSize, maxLines = 1)

        Text(formatUtil.formatAmountOfMoney(amount), Modifier.width(90.dp).padding(start = 4.dp), Colors.FormValueTextColor, Style.LabelledValueFontSize, fontFamily = FontFamily.Monospace, textAlign = TextAlign.End, maxLines = 1)
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