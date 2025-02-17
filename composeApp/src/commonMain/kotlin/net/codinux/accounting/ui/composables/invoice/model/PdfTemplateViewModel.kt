package net.codinux.accounting.ui.composables.invoice.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.codinux.i18n.Language
import net.codinux.i18n.LanguageTag
import net.codinux.invoicing.model.Image
import net.codinux.invoicing.model.InvoiceLanguage
import net.codinux.invoicing.pdf.InvoicePdfTemplateSettings

class PdfTemplateViewModel(settings: InvoicePdfTemplateSettings?): ViewModel() {

    private val _language = MutableStateFlow(settings?.language ?: getDefaultLanguage())

    val language: StateFlow<InvoiceLanguage> = _language.asStateFlow()

    fun languageChanged(newValue: InvoiceLanguage) {
        _language.value = newValue
    }

    private fun getDefaultLanguage(): InvoiceLanguage = when (LanguageTag.current.language) {
        Language.German -> InvoiceLanguage.German
        else -> InvoiceLanguage.English
    }


    private val _logoUrl = MutableStateFlow(settings?.logo?.imageUrl)
    val logoUrl: StateFlow<String?> = _logoUrl.asStateFlow()

    private val _logoBytes = MutableStateFlow(settings?.logo?.imageBytes)
    val logoBytes: StateFlow<ByteArray?> = _logoBytes.asStateFlow()

    private val _logoMimeType = MutableStateFlow(settings?.logo?.imageMimeType)
    val logoMimeType: StateFlow<String?> = _logoMimeType.asStateFlow()

    fun logoUrlChanged(newLogoUrl: String?, newLogoBytes: ByteArray? = null, newLogoMimeType: String? = null) {
        _logoUrl.value = newLogoUrl
        _logoBytes.value = newLogoBytes
        _logoMimeType.value = newLogoMimeType
    }


    fun toTemplateSettings() = InvoicePdfTemplateSettings(
        language.value,
        Image(logoUrl.value, logoBytes.value, logoMimeType.value)
    )

}