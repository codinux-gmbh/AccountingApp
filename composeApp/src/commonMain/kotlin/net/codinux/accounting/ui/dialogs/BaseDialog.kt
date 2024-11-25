package net.codinux.accounting.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import net.codinux.accounting.platform.Platform
import net.codinux.accounting.platform.PlatformType
import net.codinux.accounting.platform.isMobile
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.CloseButton
import net.codinux.accounting.ui.composables.HeaderText
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.Style
import net.codinux.accounting.ui.extensions.applyPlatformSpecificPaddingIf
import net.codinux.accounting.ui.extensions.copy
import net.codinux.accounting.ui.extensions.verticalScroll
import net.codinux.accounting.ui.composables.forms.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun BaseDialog(
    title: String? = null,
    centerTitle: Boolean = false,
    titleBarVisible: Boolean = true,
    confirmButtonVisible: Boolean = true,
    confirmButtonTitle: String = stringResource(Res.string.ok),
    confirmButtonEnabled: Boolean = true,
    dismissButtonTitle: String = stringResource(Res.string.cancel),
    showProgressIndicatorOnConfirmButton: Boolean = false,
    useMoreThanPlatformDefaultWidthOnMobile: Boolean = false,
    backgroundColor: Color = MaterialTheme.colors.surface,
    onDismiss: () -> Unit,
    onConfirm: (() -> Unit)? = null,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit
) {
    val overwriteDefaultWidth = useMoreThanPlatformDefaultWidthOnMobile && Platform.isMobile // TODO: check for UiType.Compact rather

    var isKeyboardVisible by remember { mutableStateOf(false) }


    Dialog(onDismissRequest = onDismiss, if (overwriteDefaultWidth) properties.copy(usePlatformDefaultWidth = false) else properties) {
        RoundedCornersCard(Modifier.let { if (overwriteDefaultWidth) it.fillMaxWidth(0.95f) else it }, backgroundColor = backgroundColor) {
            Column(Modifier.applyPlatformSpecificPaddingIf(overwriteDefaultWidth && isKeyboardVisible, 8.dp).background(backgroundColor).padding(horizontal = 8.dp).verticalScroll()) {

                if (titleBarVisible) {
                    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp).height(32.dp), verticalAlignment = Alignment.CenterVertically) {
                        HeaderText(title ?: "", Modifier.fillMaxWidth().weight(1f), textColor = Style.DialogTitleTextColor, textAlign = if (centerTitle) TextAlign.Center else TextAlign.Start)

                        if (Platform.type != PlatformType.Android) { // for iOS it's also relevant due to the missing back gesture / back button
                            CloseButton(onClick = onDismiss)
                        }
                    }
                }

                content()

                Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    TextButton(onClick = onDismiss, Modifier.weight(0.5f)) {
                        Text(dismissButtonTitle, color = Colors.CodinuxSecondaryColor, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }

                    if (confirmButtonVisible) {
                        TextButton(
                            modifier = Modifier.weight(0.5f),
                            enabled = confirmButtonEnabled,
                            onClick = { onConfirm?.invoke(); onDismiss() }
                        ) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                if (showProgressIndicatorOnConfirmButton) {
                                    CircularProgressIndicator(Modifier.padding(end = 6.dp).size(36.dp), color = Colors.CodinuxSecondaryColor)
                                }

                                Text(confirmButtonTitle, color = Colors.CodinuxSecondaryColor, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            }
        }
    }


    LaunchedEffect(Unit) {
        if (Platform.type == PlatformType.iOS) { // on iOS top dialog part gets hidden by top system bar when soft keyboard is visible -> apply system padding then
            Platform.addKeyboardVisibilityListener { visible ->
                isKeyboardVisible = visible
            }
        }
    }
}