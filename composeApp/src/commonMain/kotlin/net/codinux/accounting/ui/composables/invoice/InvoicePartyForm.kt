package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.forms.Select
import net.codinux.accounting.ui.composables.invoice.model.PartyViewModel
import net.codinux.accounting.ui.config.Style
import net.codinux.accounting.ui.extensions.widthForScreen
import net.codinux.invoicing.model.codes.Country


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


    InvoiceTextField(Res.string.name, name, true) { viewModel.nameChanged(it) }

    InvoiceTextField(Res.string.address, address, true) { viewModel.addressChanged(it) }

    Row(Modifier.fillMaxWidth().padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
        InvoiceTextField(Res.string.postal_code, postalCode, true, Modifier.widthForScreen(isCompactScreen, 114.dp, 130.dp).padding(end = 8.dp), KeyboardType.Ascii) { viewModel.postalCodeChanged(it) }

        InvoiceTextField(Res.string.city, city, true, Modifier.weight(1f)) { viewModel.cityChanged(it) }

        Select(Res.string.country, Country.entries.sortedBy { it.englishName }, country, { viewModel.countryChanged(it) }, // TODO: sort by user's displayName
            { if (isCompactScreen) it.alpha2Code else it.englishName }, Modifier.padding(start = 8.dp).widthForScreen(isCompactScreen, 88.dp, 225.dp), dropDownWidth = 300.dp) { country ->
            Text(country.englishName) // TODO: translate
        }
    }

    Row(Modifier.fillMaxWidth().padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
        InvoiceTextField(Res.string.email, email, modifier = Modifier.weight(0.5f).padding(end = 4.dp), keyboardType = KeyboardType.Email) { viewModel.emailChanged(it) }

        InvoiceTextField(Res.string.phone, phone, modifier = Modifier.weight(0.5f).padding(start = 4.dp), keyboardType = KeyboardType.Phone) { viewModel.phoneChanged(it) }
    }

    InvoiceTextField(if (isSupplier) Res.string.vat_id_or_tax_number_may_required else Res.string.vat_id_or_tax_number, vatId, keyboardType = KeyboardType.Ascii) { viewModel.vatIdChanged(it) }
}