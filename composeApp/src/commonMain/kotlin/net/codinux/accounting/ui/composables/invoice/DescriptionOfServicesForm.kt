package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import net.codinux.accounting.ui.config.Style
import org.jetbrains.compose.resources.stringResource


private val VerticalRowPadding = Style.FormVerticalRowPadding

@Composable
fun DescriptionOfServicesForm(viewModel: DescriptionOfServicesViewModel, isCompactScreen: Boolean) {

    val currency by viewModel.currency.collectAsState()

    val invoiceItems by viewModel.items.collectAsState()


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
        Row(Modifier.fillMaxWidth().padding(top = 12.dp).height(30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(Res.string.delivered_goods_or_provided_services), fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)

            Spacer(Modifier.weight(1f))

            TextButton({ viewModel.itemAdded(InvoiceItemViewModel()) }, contentPadding = PaddingValues(0.dp)) {
                Icon(Icons.Outlined.Add, "Add invoice item", Modifier.width(48.dp).fillMaxHeight(), Colors.CodinuxSecondaryColor)
            }
        }

        invoiceItems.forEach { item ->
            InvoiceItemForm(item)
        }
    }

}