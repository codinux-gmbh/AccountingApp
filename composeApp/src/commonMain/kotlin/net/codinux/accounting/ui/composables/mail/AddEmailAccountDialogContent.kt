package net.codinux.accounting.ui.composables.mail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.forms.*
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.Style
import net.codinux.accounting.ui.extensions.ImeNext
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


private val VerticalRowPadding = 2.dp

@Composable
fun AddEmailAccountDialogContent() {

    var receiveEmails = remember { mutableStateOf(true) }
    var receiveEmailsUsername = remember { mutableStateOf("") }
    var receiveEmailsPassword = remember { mutableStateOf("") }
    var receiveEmailsImapServerAddress = remember { mutableStateOf("") }
    var receiveEmailsPort = remember { mutableStateOf(993) }

    var sendEmails = remember { mutableStateOf(false) }
    var sendEmailsUsername = remember { mutableStateOf("") }
    var sendEmailsPassword = remember { mutableStateOf("") }
    var sendEmailsImapServerAddress = remember { mutableStateOf("") }
    var sendEmailsPort = remember { mutableStateOf(587) }


    Column {
        MailAccountForm(Res.string.receive_emails, receiveEmails, receiveEmailsUsername, receiveEmailsPassword, Res.string.imap_server_address, receiveEmailsImapServerAddress, receiveEmailsPort)

        MailAccountForm(Res.string.send_emails, sendEmails, sendEmailsUsername, sendEmailsPassword, Res.string.smtp_server_address, sendEmailsImapServerAddress, sendEmailsPort, 12.dp)
    }
}

@Composable
private fun MailAccountForm(accountLabel: StringResource, configureAccount: MutableState<Boolean>, username: MutableState<String>, password: MutableState<String>, serverAddressLabel: StringResource, serverAddress: MutableState<String>, port: MutableState<Int>, topPadding: Dp = 0.dp) {
    val enabled = configureAccount.value

    RoundedCornersCard(Modifier.fillMaxWidth().padding(top = topPadding)) {
        Column(Modifier.padding(all = Style.FormCardPadding)) {
            Row(Modifier.fillMaxWidth().clickable { configureAccount.value = !configureAccount.value }, verticalAlignment = Alignment.CenterVertically) {
                BooleanOption({ SectionHeader(stringResource(accountLabel), false) }, configureAccount.value) { configureAccount.value = it }
            }

            MailFormTextField(username, Res.string.username, enabled)

            PasswordTextField(password.value, enabled = enabled, modifier = Modifier.fillMaxWidth().padding(top = VerticalRowPadding), keyboardOptions = KeyboardOptions.ImeNext) { password.value = it }

            Row(Modifier.fillMaxWidth().padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
                MailFormTextField(serverAddress, serverAddressLabel, enabled, Modifier.weight(1f).padding(end = 12.dp))

                OutlinedNumberTextField(
                    Int::class,
                    port.value,
                    { port.value = it },
                    Modifier.width(100.dp),
                    label = { Text(stringResource(Res.string.port), color = Colors.PlaceholderTextColor, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    enabled = enabled,
                    backgroundColor = MaterialTheme.colors.surface,
                    keyboardOptions = KeyboardOptions.ImeNext
                )
            }
        }
    }
}

@Composable
private fun MailFormTextField(value: MutableState<String>, labelResource: StringResource, enabled: Boolean = true, modifier: Modifier = Modifier.fillMaxWidth().padding(top = VerticalRowPadding)) {
    OutlinedTextField(
        value.value,
        { value.value = it },
        modifier,
        label = { Text(stringResource(labelResource), color = Colors.PlaceholderTextColor, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        enabled = enabled,
        keyboardOptions = KeyboardOptions.ImeNext
    )
}