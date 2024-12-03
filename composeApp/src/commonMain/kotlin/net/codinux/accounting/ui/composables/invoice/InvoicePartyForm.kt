package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.forms.OutlinedTextField
import net.codinux.accounting.ui.composables.invoice.model.PartyViewModel
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.Style
import net.codinux.accounting.ui.extensions.ImeNext
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


private val VerticalRowPadding = Style.FormVerticalRowPadding

@Composable
fun InvoicePartyForm(viewModel: PartyViewModel) {

    val name by viewModel.name.collectAsState()

    val address by viewModel.address.collectAsState()

    val postalCode by viewModel.postalCode.collectAsState()

    val city by viewModel.city.collectAsState()


    val phone by viewModel.phone.collectAsState()

    val email by viewModel.email.collectAsState()


    val vatId by viewModel.vatId.collectAsState()


    InvoiceTextField(Res.string.name, name) { viewModel.nameChanged(it) }

    InvoiceTextField(Res.string.address, address) { viewModel.addressChanged(it) }

    Row(Modifier.fillMaxWidth().padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
        InvoiceTextField(Res.string.postal_code, postalCode, Modifier.width(130.dp).height(56.dp).padding(end = 12.dp), KeyboardType.Ascii) { viewModel.postalCodeChanged(it) }

        InvoiceTextField(Res.string.city, city, Modifier.weight(1f)) { viewModel.cityChanged(it) }
    }

    InvoiceTextField(Res.string.email, email, keyboardType = KeyboardType.Email) { viewModel.emailChanged(it) }

    InvoiceTextField(Res.string.phone, phone, keyboardType = KeyboardType.Phone) { viewModel.phoneChanged(it) }

    InvoiceTextField(Res.string.vat_id_or_tax_number, vatId, keyboardType = KeyboardType.Ascii) { viewModel.vatIdChanged(it) }
}

@Composable
private fun InvoiceTextField(labelResource: StringResource, value: String, modifier: Modifier = Modifier.fillMaxWidth().padding(top = VerticalRowPadding), keyboardType: KeyboardType = KeyboardType.Text, valueChanged: (String) -> Unit) {
    OutlinedTextField(
        value,
        { valueChanged(it) },
        modifier,
        label = { Text(stringResource(labelResource), color = Colors.PlaceholderTextColor, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        keyboardOptions = KeyboardOptions.ImeNext.copy(keyboardType = keyboardType)
    )
}