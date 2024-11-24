package net.codinux.accounting.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.appskeleton.BottomToolbar
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.tabs.*

private val uiState = DI.uiState

@Composable
fun MainScreen() {

    val selectedTab = uiState.selectedMainScreenTab.collectAsState().value


    Scaffold(
        bottomBar = { BottomToolbar(selectedTab) },
        backgroundColor = Colors.Zinc100,
    ) { scaffoldPadding -> // scaffoldPadding contains e.g. the size of the bottom toolbar

        Column(Modifier.fillMaxWidth().padding(scaffoldPadding).padding(horizontal = 10.dp)) {
            when (selectedTab) {
                MainScreenTab.Postings -> PostingsTab()
                MainScreenTab.BankAccounts -> BankAccountsTab()
                MainScreenTab.Invoices -> InvoicesTab()
                MainScreenTab.Mails -> MailsTab()
            }
        }
    }
}