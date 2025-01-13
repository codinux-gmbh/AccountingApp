package net.codinux.accounting.domain.common.model.localization

import org.jetbrains.compose.resources.StringResource

class MayUntranslatedDisplayName<T>(
    value: T,
    displayName: String,
    val stringResourceForUntranslatedDisplayName: StringResource? = null,
) : DisplayName<T>(value, displayName)