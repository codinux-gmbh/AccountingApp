package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.config.Style
import net.codinux.invoicing.model.BigDecimal
import net.codinux.invoicing.model.TotalAmounts
import net.codinux.invoicing.model.codes.Currency
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


private val formatUtil = DI.formatUtil

private val AmountLabelWidth = 60.dp
private val AmountLabelAndValueSpacing = 4.dp
private val AmountValueWidth = 126.dp
private val SeparatorWidth = AmountLabelWidth + AmountValueWidth // don't know why but adding AmountLabelAndValueSpacing would be too much

@Composable
fun TotalAmountsView(currency: Currency, totals: TotalAmounts, showSeparatorFromPreviousElement: Boolean) {
    if (showSeparatorFromPreviousElement) {
        Row(Modifier.fillMaxWidth().padding(top = 6.dp), Arrangement.End, Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f))
            Divider(Modifier.width(SeparatorWidth), Colors.ItemDividerColor)
        }
    }

    AmountRow(Res.string.net_amount, totals.lineTotalAmount, currency)
    AmountRow(Res.string.vat_amount, totals.taxTotalAmount, currency)

    Row(Modifier.fillMaxWidth().padding(top = 6.dp), Arrangement.End, Alignment.CenterVertically) {
        Spacer(Modifier.weight(1f))
        Divider(Modifier.width(SeparatorWidth), Color.Black, 1.5.dp)
    }

    AmountRow(Res.string.total_amount, totals.duePayableAmount, currency)
}

@Composable
private fun AmountRow(label: StringResource, amount: BigDecimal, currency: Currency) {
    Row(Modifier.fillMaxWidth().padding(top = 6.dp), Arrangement.End, Alignment.CenterVertically) {
        Spacer(Modifier.weight(1f))

        Text(stringResource(label), Modifier.width(AmountLabelWidth).padding(end = AmountLabelAndValueSpacing), Colors.FormLabelTextColor, Style.LabelledValueFontSize, maxLines = 1)

        Text(formatUtil.formatAmountOfMoney(amount, currency), Modifier.width(AmountValueWidth), Colors.FormValueTextColor, Style.LabelledValueFontSize, fontFamily = FontFamily.Monospace, textAlign = TextAlign.End, maxLines = 1)
    }
}