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
import androidx.compose.ui.unit.dp
import net.codinux.accounting.platform.Platform
import net.codinux.accounting.platform.isCompactScreen
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.forms.OutlinedNumberTextField
import net.codinux.accounting.ui.composables.forms.RoundedCornersCard
import net.codinux.accounting.ui.composables.forms.Select
import net.codinux.accounting.ui.composables.invoice.model.InvoiceItemViewModel
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.extensions.ImeNext
import net.codinux.invoicing.model.codes.UnitOfMeasure
import org.jetbrains.compose.resources.StringResource
import java.math.BigDecimal


private val isCompactScreen = Platform.isCompactScreen

private val SmallerFieldsWidth = 92.dp

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
                    InvoiceTextField(Res.string.name, name, true) { item.nameChanged(it) }
                }

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        InvoiceItemNumberTextField(Res.string.quantity, quantity, true) { item.quantityChanged(it) }
                    }

                    Column(Modifier.padding(start = FieldsSpace).weight(1f)) {
                        SelectUnit(unit) { item.unitChanged(it) }
                    }

                    Column(Modifier.padding(start = FieldsSpace).weight(1f)) {
                        InvoiceItemNumberTextField(Res.string.unit_price, unitPrice, true) { item.unitPriceChanged(it) }
                    }

                    Column(Modifier.padding(start = FieldsSpace).weight(1f)) {
                        InvoiceItemNumberTextField(Res.string.vat_rate, vatRate, true) { item.vatRateChanged(it) }
                    }
                }
            } else { // on large screens one line
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        InvoiceTextField(Res.string.name, name, true) { item.nameChanged(it) }
                    }

                    Column(Modifier.padding(start = FieldsSpace).width(SmallerFieldsWidth)) {
                        InvoiceItemNumberTextField(Res.string.quantity, quantity, true) { item.quantityChanged(it) }
                    }

                    Column(Modifier.padding(start = FieldsSpace).width(SmallerFieldsWidth)) {
                        SelectUnit(unit) { item.unitChanged(it) }
                    }

                    Column(Modifier.padding(start = FieldsSpace).width(SmallerFieldsWidth)) {
                        InvoiceItemNumberTextField(Res.string.unit_price, unitPrice, true) { item.unitPriceChanged(it) }
                    }

                    Column(Modifier.padding(start = FieldsSpace).width(SmallerFieldsWidth)) {
                        InvoiceItemNumberTextField(Res.string.vat_rate, vatRate, true) { item.vatRateChanged(it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun InvoiceItemNumberTextField(labelResource: StringResource, value: BigDecimal?, required: Boolean = false, modifier: Modifier = Modifier.fillMaxWidth(), valueChanged: (BigDecimal?) -> Unit) {
    OutlinedNumberTextField(
        BigDecimal::class,
        value,
        valueChanged,
        modifier,
        required = required,
        label = labelResource,
        backgroundColor = MaterialTheme.colors.surface,
        keyboardOptions = KeyboardOptions.ImeNext
    )
}

@Composable
private fun SelectUnit(value: UnitOfMeasure?, onValueChanged: (UnitOfMeasure?) -> Unit) {

    Select(Res.string.unit, UnitOfMeasure.entries.sortedBy { it.englishName }, value, { onValueChanged(it) }, { it?.symbol ?: it?.code ?: "" }, // TODO: sort by user's display name
        Modifier.width(150.dp), dropDownWidth = 300.dp, backgroundColor = MaterialTheme.colors.surface) { unit ->
        Text(unit?.let { "${unit.englishName} (${unit.symbol ?: unit.code})" } ?: "")   // TODO: translate
    }

}