package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.codinux.accounting.resources.Res
import net.codinux.accounting.resources.delivered_goods_or_provided_services
import net.codinux.accounting.ui.composables.invoice.model.DescriptionOfServicesViewModel
import net.codinux.accounting.ui.composables.invoice.model.InvoiceItemViewModel
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.config.Style
import net.codinux.invoicing.model.TotalAmounts
import org.jetbrains.compose.resources.stringResource


private val calculationService = DI.calculationService

private val VerticalRowPadding = Style.FormVerticalRowPadding

@Composable
fun DescriptionOfServicesForm(viewModel: DescriptionOfServicesViewModel, isCompactScreen: Boolean) {

    val currency by viewModel.currency.collectAsState()

    val invoiceItems by viewModel.items.collectAsState()

    val quantities = invoiceItems.map { it.quantity.collectAsState().value } // to be notified if a quantity, unit price or vat rate changes

    val unitPrices = invoiceItems.map { it.unitPrice.collectAsState().value }

    val vatRates = invoiceItems.map { it.vatRate.collectAsState().value }

    val totalAmounts by produceState(TotalAmounts.Zero, invoiceItems, quantities, unitPrices, vatRates) {
        // TODO: show error if calculation fails (e.g. no internet connection)
        value = calculationService.calculateTotalAmounts(invoiceItems)
            ?: TotalAmounts.Zero
    }


    if (isCompactScreen) {
        Column(Modifier.fillMaxWidth().padding(top = VerticalRowPadding)) {
            ServiceDateForm(viewModel, isCompactScreen)

            Row(Modifier.fillMaxWidth().padding(top = VerticalRowPadding), Arrangement.End, Alignment.CenterVertically) {
                SelectCurrency(currency) { viewModel.currencyChanged(it) }
            }
        }
    } else {
        Row(Modifier.fillMaxWidth().padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
            ServiceDateForm(viewModel, isCompactScreen)

            Spacer(Modifier.weight(1f))

            SelectCurrency(currency) { viewModel.currencyChanged(it) }
        }
    }

    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(top = Style.SectionTopPadding).height(30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(Res.string.delivered_goods_or_provided_services), fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)

            Spacer(Modifier.weight(1f))

            TextButton({ viewModel.itemAdded(InvoiceItemViewModel()) }, Modifier.width(48.dp), contentPadding = PaddingValues(0.dp)) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.End) {
                    Icon(Icons.Outlined.Add, "Add invoice item", Modifier.fillMaxHeight(), Colors.HighlightedControlColor)
                }
            }
        }

        invoiceItems.forEach { item ->
            InvoiceItemForm(item) { viewModel.removeItem(item) }
        }

        TotalAmountsView(currency, totalAmounts, false)
    }

}