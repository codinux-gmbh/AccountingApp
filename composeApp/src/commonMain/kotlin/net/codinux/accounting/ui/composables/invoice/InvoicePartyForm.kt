package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.forms.Select
import net.codinux.accounting.ui.composables.invoice.model.PartyViewModel
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.config.Style
import net.codinux.accounting.ui.extensions.widthForScreen


private val service = DI.invoiceService

private val VerticalRowPadding = Style.FormVerticalRowPadding

@Composable
fun InvoicePartyForm(viewModel: PartyViewModel, isSupplier: Boolean, isCompactScreen: Boolean) {

    val name by viewModel.name.collectAsState()

    val address by viewModel.address.collectAsState()

    val postalCode by viewModel.postalCode.collectAsState()

    val city by viewModel.city.collectAsState()

    val country by viewModel.country.collectAsState()


    val phone by viewModel.phone.collectAsState()

    val email by viewModel.email.collectAsState()


    val vatId by viewModel.vatId.collectAsState()


    val countryDisplayNames = service.getCountryDisplayNamesSorted()


    var showAllFields by remember { mutableStateOf(false) }


    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        InvoiceTextField(Res.string.name, name, true, Modifier.weight(1f).padding(end = 6.dp)) { viewModel.nameChanged(it) }

        IconButton({ showAllFields = !showAllFields }, Modifier.width(48.dp).height(Style.TextFieldsHeight)) {
            Icon(if (showAllFields) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, "Toggle show all or only most common Invoice Party fields")
        }
    }

    InvoiceTextField(Res.string.address, address, true) { viewModel.addressChanged(it) }

    Row(Modifier.fillMaxWidth().padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
        InvoiceTextField(Res.string.postal_code, postalCode, true, Modifier.widthForScreen(isCompactScreen, 114.dp, 130.dp).padding(end = 8.dp), KeyboardType.Ascii) { viewModel.postalCodeChanged(it) }

        InvoiceTextField(Res.string.city, city, true, Modifier.weight(1f)) { viewModel.cityChanged(it) }

        val countryDisplayName = countryDisplayNames.all.first { it.value == country }

        Select(Res.string.country, countryDisplayNames.preferredValues /*+ countryDisplayNames.minorValues*/, countryDisplayName,
            { viewModel.countryChanged(it.value) }, { if (isCompactScreen) it.value.alpha2Code else countryDisplayName.displayName }, Modifier.padding(start = 8.dp).widthForScreen(isCompactScreen, 88.dp, 225.dp),
            dropDownWidth = 300.dp, /*addSeparatorAfterItem = countryDisplayNames.preferredValues.size*/) { country ->
            Text(country.displayName)
        }
    }

    if (showAllFields == false) {
        Row(Modifier.fillMaxWidth().padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
            InvoiceTextField(if (isSupplier) Res.string.vat_id_or_tax_number_may_required else Res.string.vat_id_or_tax_number, vatId, modifier = Modifier.weight(0.5f).padding(end = 4.dp), keyboardType = KeyboardType.Ascii) { viewModel.vatIdChanged(it) }

            InvoiceTextField(Res.string.email, email, modifier = Modifier.weight(0.5f).padding(start = 4.dp), keyboardType = KeyboardType.Email) { viewModel.emailChanged(it) }
        }
    } else {
        Row(Modifier.fillMaxWidth().padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
            InvoiceTextField(Res.string.email, email, modifier = Modifier.weight(0.5f).padding(end = 4.dp), keyboardType = KeyboardType.Email) { viewModel.emailChanged(it) }

            InvoiceTextField(Res.string.phone, phone, modifier = Modifier.weight(0.5f).padding(start = 4.dp), keyboardType = KeyboardType.Phone) { viewModel.phoneChanged(it) }
        }

        InvoiceTextField(if (isSupplier) Res.string.vat_id_or_tax_number_may_required else Res.string.vat_id_or_tax_number, vatId, keyboardType = KeyboardType.Ascii) { viewModel.vatIdChanged(it) }
    }
}