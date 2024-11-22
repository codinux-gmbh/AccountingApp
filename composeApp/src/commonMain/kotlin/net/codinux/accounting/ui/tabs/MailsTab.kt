package net.codinux.accounting.ui.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.codinux.accounting.ui.composables.mail.MailsList
import net.codinux.accounting.ui.config.DI
import net.codinux.invoicing.mail.MailWithInvoice


private val mailService = DI.mailService

@Composable
fun MailsTab() {

    var isInitialized by remember { mutableStateOf(false) }

    var mails by remember { mutableStateOf(listOf<MailWithInvoice>()) }

    val coroutineScope = rememberCoroutineScope()


    Column(Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 10.dp)) {
        MailsList(mails)
    }


    LaunchedEffect(isInitialized) {
        coroutineScope.launch(Dispatchers.IO) {
            mails = mailService.loadPersistedMails()

            isInitialized = true
        }
    }

}