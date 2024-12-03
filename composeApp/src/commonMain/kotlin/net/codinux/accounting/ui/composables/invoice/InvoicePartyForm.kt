package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.invoice.model.PartyViewModel
import net.codinux.accounting.ui.config.Style


private val VerticalRowPadding = Style.FormVerticalRowPadding

@Composable
fun InvoicePartyForm(viewModel: PartyViewModel, isSupplier: Boolean) {

    val name by viewModel.name.collectAsState()

    val address by viewModel.address.collectAsState()

    val postalCode by viewModel.postalCode.collectAsState()

    val city by viewModel.city.collectAsState()


    val phone by viewModel.phone.collectAsState()

    val email by viewModel.email.collectAsState()


    val vatId by viewModel.vatId.collectAsState()


    InvoiceTextField(Res.string.name, name, true) { viewModel.nameChanged(it) }

    InvoiceTextField(Res.string.address, address, true) { viewModel.addressChanged(it) }

    Row(Modifier.fillMaxWidth().padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
        InvoiceTextField(Res.string.postal_code, postalCode, true, Modifier.width(130.dp).height(56.dp).padding(end = 12.dp), KeyboardType.Ascii) { viewModel.postalCodeChanged(it) }

        InvoiceTextField(Res.string.city, city, true, Modifier.weight(1f)) { viewModel.cityChanged(it) }
    }

    InvoiceTextField(Res.string.email, email, keyboardType = KeyboardType.Email) { viewModel.emailChanged(it) }

    InvoiceTextField(Res.string.phone, phone, keyboardType = KeyboardType.Phone) { viewModel.phoneChanged(it) }

    InvoiceTextField(if (isSupplier) Res.string.vat_id_or_tax_number_may_required else Res.string.vat_id_or_tax_number, vatId, keyboardType = KeyboardType.Ascii) { viewModel.vatIdChanged(it) }
}