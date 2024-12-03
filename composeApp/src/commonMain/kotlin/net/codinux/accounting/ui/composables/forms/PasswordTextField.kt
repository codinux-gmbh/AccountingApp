package net.codinux.accounting.ui.composables.forms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import net.codinux.accounting.resources.Res
import net.codinux.accounting.resources.password
import org.jetbrains.compose.resources.StringResource

@Composable // try BasicSecureTextField
fun PasswordTextField(
    password: String = "",
    label: StringResource = Res.string.password,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions? = null,
    isError: Boolean = false,
    forceHidePassword: Boolean? = null,
    onEnterPressed: (() -> Unit)? = null,
    onChange: (String) -> Unit
) {

    var passwordVisible by remember { mutableStateOf(false) }

    if (forceHidePassword != null) {
        passwordVisible = !!!forceHidePassword
    }

    OutlinedTextField(
        value = password,
        onValueChange = { onChange(it) },
        label = label,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        isError = isError,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val visibilityIcon = if (passwordVisible) {
                Icons.Filled.VisibilityOff
            } else {
                Icons.Filled.Visibility
            }
            Icon(
                visibilityIcon,
                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                modifier = Modifier.size(24.dp).clickable { passwordVisible = !passwordVisible }
            )
        },
        keyboardOptions = keyboardOptions?.copy(keyboardType = KeyboardType.Password) ?: KeyboardOptions(keyboardType = KeyboardType.Password),
        onEnterPressed = onEnterPressed
    )
}