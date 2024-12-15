package net.codinux.accounting.ui.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.mail.MailsList
import net.codinux.accounting.ui.dialogs.AddEmailAccountDialog
import net.codinux.accounting.ui.state.EmailsUiState
import org.jetbrains.compose.resources.stringResource

@Composable
fun MailsTab(uiState: EmailsUiState) {

    val mails = uiState.mails.collectAsState().value

    val showAddMailAccountDialog by uiState.showAddMailAccountDialog.collectAsState()


    if (mails.isEmpty()) {
        Column(Modifier.fillMaxSize().padding(horizontal = 36.dp), Arrangement.Center, Alignment.CenterHorizontally) {
            Text(stringResource(Res.string.reason_for_adding_email_account), fontSize = 18.sp, textAlign = TextAlign.Center)
        }
    } else {
        MailsList(uiState, mails)
    }


    if (showAddMailAccountDialog) {
        AddEmailAccountDialog()
    }

}