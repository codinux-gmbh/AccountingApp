package net.codinux.accounting.ui.composables.forms

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import net.codinux.log.Log
import java.math.BigDecimal
import kotlin.reflect.KClass


private val FloatingPointDataTypes = listOf(BigDecimal::class, Double::class, Float::class)


@Composable
fun <T : Number> OutlinedNumberTextField(
    valueClass: KClass<T>,
    value: T? = null,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
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
) {

    // remember(value) is required so that on changes to value, e.g. after restoring historical data, the init method gets called again
    var enteredValue by remember(value) { mutableStateOf(if (value is BigDecimal) value.toPlainString() else (value?.toString() ?: "")) }

    // optionally starts with a minus, followed by any number of numbers. Optionally ends with a decimal separator and any number of numbers.
    val decimalRegex = Regex("^-?\\d*([.,]\\d*)?")


    fun mapEnteredString(value: String): T {
        val valueFixed = value.replace(',', '.')

        val mapped = if (valueClass == BigDecimal::class) BigDecimal(valueFixed)
        else if (valueClass == Double::class) valueFixed.toDouble()
        else if (valueClass == Float::class) valueFixed.toFloat()
        else if (valueClass == Long::class) valueFixed.toLong()
        else valueFixed.toInt()

        return mapped as T
    }


    OutlinedTextField(
        value = enteredValue,
        onValueChange = { newValue ->
            if (newValue.isEmpty() || decimalRegex.matches(newValue)) {
                enteredValue = newValue
                try {
                    if (newValue.isNotBlank() && newValue != "-") { // "" and "-" are valid values to enter but will not map to a number
                        onValueChange(mapEnteredString(newValue))
                    }
                } catch (e: Throwable) { // e.g. it's valid to enter "-", but this cannot get mapped to a number yet
                    Log.warn(e) { "Could not map value '$newValue' to $valueClass" }
                }
            }
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle.copy(textAlign = TextAlign.End),
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        backgroundColor = backgroundColor,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions.copy(keyboardType = if (valueClass in FloatingPointDataTypes) KeyboardType.Decimal else KeyboardType.Number),
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        onEnterPressed = onEnterPressed
    )
}