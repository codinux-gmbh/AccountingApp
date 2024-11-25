package net.codinux.accounting.ui.dialogs

import androidx.compose.runtime.Composable
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.mail.AddEmailAccountDialogContent
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddEmailAccountDialog() {

    BaseDialog(
        title = stringResource(Res.string.add_email_account),
        confirmButtonTitle = stringResource(Res.string.add),
        backgroundColor = Colors.MainBackgroundColor,
        useMoreThanPlatformDefaultWidthOnMobile = true,
        onConfirm = { },
        onDismiss = { DI.uiState.showAddMailAccountDialog.value = false }
    ) {
        AddEmailAccountDialogContent()
    }
}