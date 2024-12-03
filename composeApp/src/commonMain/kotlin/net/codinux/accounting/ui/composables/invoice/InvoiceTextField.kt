package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import net.codinux.accounting.ui.composables.forms.OutlinedTextField
import net.codinux.accounting.ui.config.Style
import net.codinux.accounting.ui.extensions.ImeNext
import org.jetbrains.compose.resources.StringResource

@Composable
fun InvoiceTextField(label: StringResource, value: String, modifier: Modifier = Modifier.fillMaxWidth().padding(top = Style.FormVerticalRowPadding), keyboardType: KeyboardType = KeyboardType.Text, valueChanged: (String) -> Unit) {
    OutlinedTextField(
        value,
        { valueChanged(it) },
        modifier,
        label = label,
        backgroundColor = MaterialTheme.colors.surface,
        keyboardOptions = KeyboardOptions.ImeNext.copy(keyboardType = keyboardType)
    )
}