package net.codinux.accounting.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.HeaderText
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.Config.NewLine
import net.codinux.accounting.ui.extensions.verticalScroll
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorDialog(
    text: String,
    title: StringResource? = null,
    exception: Throwable? = null,
    confirmButtonText: String = stringResource(Res.string.ok),
    onDismiss: (() -> Unit)? = null
) {

    val effectiveText = if (exception == null) text else {
        "$text${NewLine}${NewLine}${stringResource(Res.string.error_message)}:${NewLine}${exception.stackTraceToString()}"
    }


    AlertDialog(
        text = { Text(effectiveText, Modifier.verticalScroll()) },
        title = { title?.let {
            HeaderText(stringResource(title), Modifier.fillMaxWidth(), TextAlign.Center)
        } },
        modifier = Modifier.let { if (exception != null) it.fillMaxWidth(0.95f) else it },
        properties = if (exception == null) DialogProperties() else DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onDismiss?.invoke() },
        confirmButton = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton({ onDismiss?.invoke() }, Modifier.fillMaxWidth()) {
                    Text(confirmButtonText, color = Colors.HighlightedTextColor, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
                }
            }
        }
    )

}