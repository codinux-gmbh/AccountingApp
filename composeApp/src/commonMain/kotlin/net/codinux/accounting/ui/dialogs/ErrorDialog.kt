package net.codinux.accounting.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.HeaderText
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.Config.NewLine
import net.codinux.accounting.ui.extensions.verticalScroll
import net.codinux.invoicing.model.dto.SerializableException
import net.codinux.invoicing.web.WebClientException
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorDialog(
    text: String,
    title: StringResource? = null,
    exception: SerializableException? = null,
    confirmButtonText: String = stringResource(Res.string.ok),
    onDismiss: (() -> Unit)? = null
) {

    val effectiveText = if (exception == null) text else {
        val additionalInfo = if (exception.originalException is WebClientException) {
            val webClientException = exception.originalException as WebClientException
            when {
                webClientException.isNetworkError -> Res.string.error_message_network_error
                webClientException.isClientError -> Res.string.error_message_client_error
                webClientException.isServerError -> Res.string.error_message_server_error
                else -> null
            }
        } else null

        "$text${additionalInfo?.let { "$NewLine$NewLine${stringResource(it)}" }}${NewLine}${NewLine}${stringResource(Res.string.error_message)}:" +
                "${NewLine}${exception.cause?.type ?: exception.type} ${exception.message}"
    }

    var showStacktrace by remember { mutableStateOf(false) }


    AlertDialog(
        title = { title?.let {
            HeaderText(stringResource(title), Modifier.fillMaxWidth(), TextAlign.Center)
        } },
        modifier = Modifier.fillMaxWidth(0.95f),
        properties = DialogProperties(usePlatformDefaultWidth = exception == null),
        onDismissRequest = { onDismiss?.invoke() },
        confirmButton = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton({ onDismiss?.invoke() }, Modifier.fillMaxWidth()) {
                    Text(confirmButtonText, color = Colors.HighlightedTextColor, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
                }
            }
        },
        text = {
            // show stacktrace only for developers
            if (exception?.stackTrace == null || net.codinux.kotlin.platform.Environment().isRunningInDebugMode == false) {
                Text(effectiveText, Modifier.verticalScroll())
            } else {
                Column(Modifier.fillMaxWidth()) {
                    Text(effectiveText, Modifier.verticalScroll())

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton({ showStacktrace = !!!showStacktrace }) {
                            Text("Show Stacktrace", color = Colors.HighlightedTextColor)
                        }
                    }

                    if (showStacktrace) { // whatever you try to restrict stacktrace's text field height, on Android the alert dialog will break
                        Text(exception.stackTrace ?: "", Modifier.fillMaxHeight(0.6f).verticalScroll())
                    }
                }
            }
        },
    )

}