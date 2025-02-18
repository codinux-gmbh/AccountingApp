package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import net.codinux.accounting.resources.*
import net.codinux.accounting.resources.Res
import net.codinux.accounting.resources.invoice_language
import net.codinux.accounting.resources.logo_url
import net.codinux.accounting.ui.composables.forms.OutlinedTextField
import net.codinux.accounting.ui.composables.forms.Select
import net.codinux.accounting.ui.composables.invoice.model.PdfTemplateViewModel
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.extensions.ImeNext
import net.codinux.accounting.ui.extensions.extension
import net.codinux.accounting.ui.extensions.parent
import net.codinux.invoicing.model.InvoiceLanguage
import org.jetbrains.compose.resources.stringResource

private val supportedImageFiles = listOf("png", "jpg", "jpeg", "gif", "tif", "tiff", "bmp"/*, "svg"*/)

@Composable
fun InvoicePdfSettingsForm(viewModel: PdfTemplateViewModel, isCompactScreen: Boolean) {

    val selectedInvoiceLanguage by viewModel.language.collectAsState()

    val invoiceLogoUrl by viewModel.logoUrl.collectAsState()

    val lastOpenLogoDirectory by viewModel.lastOpenLogoDirectory.collectAsState()

    val coroutineScope = rememberCoroutineScope()


    fun getMimeType(extension: String): String? = when (extension) {
        "png" -> "image/png"
        "jpg", "jpeg" -> "image/jpeg"
        "gif" -> "image/gif"
        "bmp" -> "image/bmp"
        "tif", "tiff" -> "image/tiff"
        "svg" -> "image/svg+xml"
        else -> null
    }

    val openLogoFileFileLauncher = rememberFilePickerLauncher(PickerType.File(supportedImageFiles), stringResource(Res.string.select_company_logo_for_invoice), lastOpenLogoDirectory) { selectedFile ->
        if (selectedFile != null) {
            coroutineScope.launch {
                viewModel.logoUrlChanged(selectedFile.path, selectedFile.readBytes(), getMimeType(selectedFile.extension))

                viewModel.lastOpenLogoDirectoryChanged(selectedFile.parent)
            }
        }
    }

    @Composable
    fun getLabel(language: InvoiceLanguage): String = when (language) {
        InvoiceLanguage.English -> stringResource(Res.string.english)
        InvoiceLanguage.German -> stringResource(Res.string.german)
    }


    Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Select(Res.string.invoice_language, InvoiceLanguage.entries, selectedInvoiceLanguage, { viewModel.languageChanged(it) },
            { getLabel(it) }, Modifier.width(if (isCompactScreen) 125.dp else 150.dp))

        OutlinedTextField(invoiceLogoUrl ?: "", { viewModel.logoUrlChanged(it.takeUnless { it.isBlank() }) },
            Modifier.weight(1f).padding(horizontal = 6.dp), label = Res.string.logo_url,
            backgroundColor = MaterialTheme.colors.surface, keyboardOptions = KeyboardOptions.ImeNext,
            //textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
            trailingIcon = { IconButton({ openLogoFileFileLauncher.launch() }, Modifier.size(48.dp)) {
                Icon(Icons.Outlined.FolderOpen, "Select file with company logo to display on invoice", tint = Colors.FormValueTextColor)
            } }
        )
    }

}