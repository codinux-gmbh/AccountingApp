package net.codinux.accounting.ui.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.composables.mail.MailsList
import net.codinux.invoicing.mail.MailWithInvoice

@Composable
fun MailsTab() {

    var mails by remember { mutableStateOf(listOf<MailWithInvoice>()) }


    Column(Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 10.dp)) {
        MailsList(mails)
    }

}