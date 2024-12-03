package net.codinux.accounting.ui.composables.forms

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import net.codinux.accounting.ui.config.Colors
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: StringResource? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    backgroundColor: Color = Color.Transparent,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    onEnterPressed: (() -> Unit)? = null
//    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors() // TODO: merge
) {
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Colors.CodinuxSecondaryColor,
        focusedLabelColor = Colors.CodinuxSecondaryColor,
        backgroundColor = backgroundColor
    )

    androidx.compose.material.OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = { Text(labelText, color = Colors.PlaceholderTextColor, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = if (onEnterPressed != null) keyboardOptions.copy(imeAction = ImeAction.Done) else keyboardOptions,
        keyboardActions = if (onEnterPressed != null) KeyboardActions(onDone = { onEnterPressed.invoke() }) else keyboardActions, // onKeyEvent { } only handles input on hardware keyboards, therefore we have also have to overwrite onDone for IME / soft keyboards
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = textFieldColors
    )
}