package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.forms.*
import net.codinux.accounting.ui.composables.forms.model.MenuItem
import net.codinux.accounting.ui.composables.invoice.model.InvoiceItemViewModel
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.config.Style
import net.codinux.accounting.ui.extensions.ImeNext
import net.codinux.invoicing.model.BigDecimal
import net.codinux.invoicing.model.codes.UnitOfMeasure
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


private val SmallerFieldsWidth = 92.dp

private val FieldsSpace = 4.dp

private val ItemPadding = 4.dp


@Composable
fun InvoiceItemForm(item: InvoiceItemViewModel, removeItemClicked: () -> Unit) {

    val isCompactScreen = DI.uiState.uiType.collectAsState().value.isCompactScreen

    val name by item.name.collectAsState()

    val quantity by item.quantity.collectAsState()

    val unit by item.unit.collectAsState()

    val unitPrice by item.unitPrice.collectAsState()

    val vatRate by item.vatRate.collectAsState()


    Spacer(Modifier.height(6.dp))

    RoundedCornersCard(Modifier.fillMaxWidth(), cornerSize = 8.dp, backgroundColor = Colors.Zinc100) {
        Column(Modifier.fillMaxWidth().padding(all = ItemPadding)) {
            if (isCompactScreen) { // on small screens we use two lines
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    InvoiceTextField(Res.string.name, name, true, Modifier.weight(1f)) { item.nameChanged(it) }

                    InvoiceItemMenu(removeItemClicked)
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

                    InvoiceItemMenu(removeItemClicked)
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

    val unitsOfMeasure = DI.invoiceService.getUnitOfMeasureDisplayNamesSorted()

    Select(Res.string.unit, unitsOfMeasure.preferredValues /*+ unitsOfMeasure.minorValues */, unitsOfMeasure.all.firstOrNull { it.value == value}, { onValueChanged(it?.value) }, { it?.shortName ?: "" },
        Modifier.width(150.dp), dropDownWidth = 300.dp, /* addSeparatorAfterItem = unitsOfMeasure.preferredValues.size, */
        backgroundColor = MaterialTheme.colors.surface, required = true) { unit ->
        Text(unit?.let { "${unit.displayName} (${unit.shortName})" } ?: "")
    }

}

@Composable
private fun InvoiceItemMenu(removeItemClicked: () -> Unit) {
    OverflowMenu(additionalMenuPadding = ItemPadding + Style.MainScreenTabHorizontalPadding + ItemPadding, items = listOf(
        MenuItem({ DeleteMenuItem() }, removeItemClicked )
    ))
}

@Composable
private fun DeleteMenuItem() {
    Row(Modifier) {
        Icon(Icons.Outlined.Delete, "Remove Invoice Item", modifier = Modifier)

        Text(stringResource(Res.string.remove), Modifier.padding(start = 4.dp))
    }
}