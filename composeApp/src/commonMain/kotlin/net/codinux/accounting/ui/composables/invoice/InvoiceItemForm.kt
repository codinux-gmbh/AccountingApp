package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.codinux.accounting.platform.Platform
import net.codinux.accounting.platform.isMobile
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.forms.OutlinedNumberTextField
import net.codinux.accounting.ui.composables.forms.RoundedCornersCard
import net.codinux.accounting.ui.composables.invoice.model.InvoiceItemViewModel
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.extensions.ImeNext
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.math.BigDecimal


private val isCompactScreen = Platform.isMobile

private val SmallerFieldsWidth = 85.dp

private val FieldsSpace = 4.dp


@Composable
fun InvoiceItemForm(item: InvoiceItemViewModel) {

    val name by item.name.collectAsState()

    val quantity by item.quantity.collectAsState()

    val unit by item.unit.collectAsState()

    val unitPrice by item.unitPrice.collectAsState()

    val vatRate by item.vatRate.collectAsState()


    Spacer(Modifier.height(6.dp))

    RoundedCornersCard(Modifier.fillMaxWidth(), cornerSize = 8.dp, backgroundColor = Colors.Zinc100) {
        Column(Modifier.fillMaxWidth().padding(all = 4.dp)) {
            if (isCompactScreen) { // on small screens we use two lines
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    InvoiceTextField(Res.string.name, name) { item.nameChanged(it) }
                }

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        InvoiceItemNumberTextField(Res.string.quantity, quantity) { item.quantityChanged(it) }
                    }

                    Column(Modifier.padding(start = FieldsSpace).weight(1f)) {
                        InvoiceTextField(Res.string.unit, unit) { item.unitChanged(it) }
                    }

                    Column(Modifier.padding(start = FieldsSpace).weight(1f)) {
                        InvoiceItemNumberTextField(Res.string.unit_price, unitPrice) { item.unitPriceChanged(it) }
                    }

                    Column(Modifier.padding(start = FieldsSpace).weight(1f)) {
                        InvoiceItemNumberTextField(Res.string.vat_rate, vatRate) { item.vatRateChanged(it) }
                    }
                }
            } else { // on large screens one line
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        InvoiceTextField(Res.string.name, name) { item.nameChanged(it) }
                    }

                    Column(Modifier.padding(start = FieldsSpace).width(SmallerFieldsWidth)) {
                        InvoiceItemNumberTextField(Res.string.quantity, quantity) { item.quantityChanged(it) }
                    }

                    Column(Modifier.padding(start = FieldsSpace).width(SmallerFieldsWidth)) {
                        InvoiceTextField(Res.string.unit, unit) { item.unitChanged(it) }
                    }

                    Column(Modifier.padding(start = FieldsSpace).width(SmallerFieldsWidth)) {
                        InvoiceItemNumberTextField(Res.string.unit_price, unitPrice) { item.unitPriceChanged(it) }
                    }

                    Column(Modifier.padding(start = FieldsSpace).width(SmallerFieldsWidth)) {
                        InvoiceItemNumberTextField(Res.string.vat_rate, vatRate) { item.vatRateChanged(it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun InvoiceItemNumberTextField(labelResource: StringResource, value: BigDecimal?, modifier: Modifier = Modifier.fillMaxWidth(), valueChanged: (BigDecimal?) -> Unit) {
    OutlinedNumberTextField(
        BigDecimal::class,
        value,
        valueChanged,
        modifier,
        label = labelResource,
        backgroundColor = MaterialTheme.colors.surface,
        keyboardOptions = KeyboardOptions.ImeNext
    )
}