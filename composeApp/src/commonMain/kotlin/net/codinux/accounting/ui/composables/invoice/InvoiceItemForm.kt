package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.codinux.accounting.platform.Platform
import net.codinux.accounting.platform.isMobile
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.forms.OutlinedNumberTextField
import net.codinux.accounting.ui.composables.forms.OutlinedTextField
import net.codinux.accounting.ui.composables.forms.RoundedCornersCard
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.extensions.ImeNext
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.math.BigDecimal
import kotlin.reflect.KMutableProperty


class EditableInvoiceItem(
    var name: String = "",
    var quantity: BigDecimal? = null,
    var unit: String = "", // TODO: this is actually the unit code consisting of 3 alphanumerical characters from EN 16931
    var unitPrice: BigDecimal? = null,
    var vatRate: BigDecimal? = null,
    var description: String? = null,
) {
    override fun toString() = "$name, $quantity x $unitPrice, $vatRate %"
}


private val isCompactScreen = Platform.isMobile

private val SmallerFieldsWidth = 85.dp

private val FieldsSpace = 4.dp


@Composable
fun InvoiceItemForm(item: EditableInvoiceItem) {

    Spacer(Modifier.height(6.dp))

    RoundedCornersCard(Modifier.fillMaxWidth(), cornerSize = 8.dp, backgroundColor = Colors.Zinc100) {
        Column(Modifier.fillMaxWidth().padding(all = 4.dp)) {
            if (isCompactScreen) { // on small screens we use two lines
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    InvoiceItemTextField(item, EditableInvoiceItem::name, Res.string.name)
                }

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        InvoiceItemNumberTextField(item, EditableInvoiceItem::quantity, Res.string.quantity)
                    }

                    Column(Modifier.padding(start = FieldsSpace).weight(1f)) {
                        InvoiceItemTextField(item, EditableInvoiceItem::unit, Res.string.unit)
                    }

                    Column(Modifier.padding(start = FieldsSpace).weight(1f)) {
                        InvoiceItemNumberTextField(item, EditableInvoiceItem::unitPrice, Res.string.unit_price)
                    }

                    Column(Modifier.padding(start = FieldsSpace).weight(1f)) {
                        InvoiceItemNumberTextField(item, EditableInvoiceItem::vatRate, Res.string.vat_rate)
                    }
                }
            } else { // on large screens one line
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        InvoiceItemTextField(item, EditableInvoiceItem::name, Res.string.name)
                    }

                    Column(Modifier.padding(start = FieldsSpace).width(SmallerFieldsWidth)) {
                        InvoiceItemNumberTextField(item, EditableInvoiceItem::quantity, Res.string.quantity)
                    }

                    Column(Modifier.padding(start = FieldsSpace).width(SmallerFieldsWidth)) {
                        InvoiceItemTextField(item, EditableInvoiceItem::unit, Res.string.unit)
                    }

                    Column(Modifier.padding(start = FieldsSpace).width(SmallerFieldsWidth)) {
                        InvoiceItemNumberTextField(item, EditableInvoiceItem::unitPrice, Res.string.unit_price)
                    }

                    Column(Modifier.padding(start = FieldsSpace).width(SmallerFieldsWidth)) {
                        InvoiceItemNumberTextField(item, EditableInvoiceItem::vatRate, Res.string.vat_rate)
                    }
                }
            }
        }
    }
}

@Composable
private fun InvoiceItemTextField(item: EditableInvoiceItem, property: KMutableProperty<String>, labelResource: StringResource, modifier: Modifier = Modifier.fillMaxWidth()) {

    var enteredValue by remember { mutableStateOf(property.getter.call(item)) }

    OutlinedTextField(
        enteredValue,
        { newValue ->
            property.setter.call(item, newValue)
            enteredValue = newValue
        },
        modifier,
        label = { Text(stringResource(labelResource), color = Colors.PlaceholderTextColor, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        backgroundColor = MaterialTheme.colors.surface,
        keyboardOptions = KeyboardOptions.ImeNext
    )
}

@Composable
private fun InvoiceItemNumberTextField(item: EditableInvoiceItem, property: KMutableProperty<BigDecimal?>, labelResource: StringResource, modifier: Modifier = Modifier.fillMaxWidth()) {
    OutlinedNumberTextField(
        BigDecimal::class,
        property.getter.call(item),
        { property.setter.call(item, it) },
        modifier,
        label = { Text(stringResource(labelResource), color = Colors.PlaceholderTextColor, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        backgroundColor = MaterialTheme.colors.surface,
        keyboardOptions = KeyboardOptions.ImeNext
    )
}