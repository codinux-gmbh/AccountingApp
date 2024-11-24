package net.codinux.accounting.ui.composables.forms

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import net.codinux.accounting.ui.config.Colors

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
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
        // a workaround: we assume textStyle is set to change text color, so we set unfocusedBorderColor according to it // TODO: fix by passing textColor to OutlinedTextField
        unfocusedBorderColor = if (textStyle != LocalTextStyle.current) textStyle.color else MaterialTheme.colors.primary.copy(alpha = ContentAlpha.high),
        focusedLabelColor = Colors.CodinuxSecondaryColor // does not work
    )

    androidx.compose.material.OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
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