package net.codinux.accounting.ui.composables.forms.model

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

open class MenuItem(
    val content: @Composable () -> Unit,
    val onClick: () -> Unit
)

class TextMenuItem(
    label: StringResource,
    onClick: () -> Unit
) : MenuItem({ Text(stringResource(label)) }, onClick)
