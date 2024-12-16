package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.codinux.accounting.resources.Res
import net.codinux.accounting.resources.currency
import net.codinux.accounting.ui.composables.forms.Select
import net.codinux.accounting.ui.config.DI
import net.codinux.invoicing.model.codes.Currency


private val currencyDisplayNames = DI.invoiceService.getCurrencyDisplayNamesSorted()

@Composable
fun SelectCurrency(value: Currency, onValueChanged: (Currency) -> Unit) {

    Select(Res.string.currency, currencyDisplayNames.preferredValues /* + currencyDisplayNames.minorValues */, currencyDisplayNames.all.first { it.value == value },
        { onValueChanged(it.value) }, { it.value.currencySymbol ?: it.value.alpha3Code },
        Modifier.width(110.dp), textStyle = TextStyle(textAlign = TextAlign.End), dropDownWidth = 300.dp,
        /*addSeparatorAfterItem = currencyDisplayNames.preferredValues.size*/) { currency ->
        Text("${currency.displayName} (${currency.value.currencySymbol ?: currency.value.alpha3Code})")
    }

}