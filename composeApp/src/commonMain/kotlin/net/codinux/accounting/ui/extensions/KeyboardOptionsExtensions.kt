package net.codinux.accounting.ui.extensions

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction


val KeyboardOptions.Companion.ImeNext: KeyboardOptions
    get() = KeyboardOptions(imeAction = ImeAction.Next)

val KeyboardOptions.Companion.ImeDone: KeyboardOptions
    get() = KeyboardOptions(imeAction = ImeAction.Done)