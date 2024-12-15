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
import net.codinux.accounting.domain.mail.model.MailAccountConfiguration
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.composables.forms.*
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.Style
import net.codinux.accounting.ui.extensions.ImeNext
import net.codinux.invoicing.email.model.EmailAccount
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


private val VerticalRowPadding = Style.FormVerticalRowPadding

@Composable
fun AddEmailAccountDialogContent(account: MailAccountConfiguration) {

    var receiveEmails = remember { mutableStateOf(true) }
    var receiveEmailsUsername = remember { mutableStateOf("") }
    var receiveEmailsPassword = remember { mutableStateOf("") }
    var receiveEmailsImapServerAddress = remember { mutableStateOf("") }
    var receiveEmailsPort = remember { mutableStateOf<Int?>(993) }

    var sendEmails = remember { mutableStateOf(false) }
    var sendEmailsUsername = remember { mutableStateOf("") }
    var sendEmailsPassword = remember { mutableStateOf("") }
    var sendEmailsSmtpServerAddress = remember { mutableStateOf("") }
    var sendEmailsPort = remember { mutableStateOf<Int?>(587) }


    Column {
        MailAccountForm(Res.string.receive_emails, receiveEmails, receiveEmailsUsername, receiveEmailsPassword, Res.string.imap_server_address, receiveEmailsImapServerAddress, receiveEmailsPort)

        MailAccountForm(Res.string.send_emails, sendEmails, sendEmailsUsername, sendEmailsPassword, Res.string.smtp_server_address, sendEmailsSmtpServerAddress, sendEmailsPort, 12.dp)
    }


    DisposableEffect(receiveEmails.value, receiveEmailsUsername.value, receiveEmailsPassword.value, receiveEmailsImapServerAddress.value, receiveEmailsPort.value) {
        account.receiveEmailConfiguration = if (receiveEmails.value == false) null else EmailAccount(receiveEmailsUsername.value, receiveEmailsPassword.value, receiveEmailsImapServerAddress.value, receiveEmailsPort.value)

        onDispose { }
    }

    DisposableEffect(sendEmails.value, sendEmailsUsername.value, sendEmailsPassword.value, sendEmailsSmtpServerAddress.value, sendEmailsPort.value) {
        if (sendEmails.value == true && sendEmailsUsername.value.isEmpty() && sendEmailsPassword.value.isEmpty() && sendEmailsSmtpServerAddress.value.isEmpty()) {
            sendEmailsUsername.value = receiveEmailsUsername.value
            sendEmailsPassword.value = receiveEmailsPassword.value
            sendEmailsSmtpServerAddress.value = receiveEmailsImapServerAddress.value
        }

        account.sendEmailConfiguration = if (sendEmails.value == false) null else EmailAccount(sendEmailsUsername.value, sendEmailsPassword.value, sendEmailsSmtpServerAddress.value, sendEmailsPort.value)

        onDispose { }
    }
}

@Composable
private fun MailAccountForm(accountLabel: StringResource, configureAccount: MutableState<Boolean>, username: MutableState<String>, password: MutableState<String>, serverAddressLabel: StringResource, serverAddress: MutableState<String>, port: MutableState<Int?>, topPadding: Dp = 0.dp) {
    val enabled = configureAccount.value

    RoundedCornersCard(Modifier.fillMaxWidth().padding(top = topPadding)) {
        Column(Modifier.padding(all = Style.FormCardPadding)) {
            Row(Modifier.fillMaxWidth().clickable { configureAccount.value = !configureAccount.value }, verticalAlignment = Alignment.CenterVertically) {
                BooleanOption({ SectionHeader(accountLabel) }, configureAccount.value) { configureAccount.value = it }
            }

            MailFormTextField(username, Res.string.username, enabled)

            PasswordTextField(password.value, enabled = enabled, required = enabled, modifier = Modifier.fillMaxWidth().padding(top = VerticalRowPadding), keyboardOptions = KeyboardOptions.ImeNext) { password.value = it }

            Row(Modifier.fillMaxWidth().padding(top = VerticalRowPadding), verticalAlignment = Alignment.CenterVertically) {
                MailFormTextField(serverAddress, serverAddressLabel, enabled, Modifier.weight(1f).padding(end = 12.dp))

                OutlinedNumberTextField(
                    Int::class,
                    port.value,
                    { port.value = it },
                    Modifier.width(100.dp),
                    label = Res.string.port,
                    enabled = enabled,
                    backgroundColor = MaterialTheme.colors.surface,
                    keyboardOptions = KeyboardOptions.ImeNext
                )
            }
        }
    }
}

@Composable
private fun MailFormTextField(value: MutableState<String>, label: StringResource, enabled: Boolean = true, modifier: Modifier = Modifier.fillMaxWidth().padding(top = VerticalRowPadding)) {
    OutlinedTextField(
        value.value,
        { value.value = it },
        modifier,
        label = label,
        enabled = enabled,
        required = enabled,
        keyboardOptions = KeyboardOptions.ImeNext
    )
}