package net.codinux.accounting.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.appskeleton.BottomToolbar
import net.codinux.accounting.ui.appskeleton.MainScreenFloatingActionButton
import net.codinux.accounting.ui.composables.StateHandler
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.tabs.*

private val uiState = DI.uiState

@Composable
fun MainScreen() {

    val selectedTab = uiState.selectedMainScreenTab.collectAsState().value


    Scaffold(
        bottomBar = { BottomToolbar(selectedTab) },
        backgroundColor = Colors.MainBackgroundColor,
        floatingActionButton = { MainScreenFloatingActionButton(uiState, selectedTab) },
    ) { scaffoldPadding -> // scaffoldPadding contains e.g. the size of the bottom toolbar

        // when removing tabs from composition tree, than tab's state, e.g. entered data, gets
        // deleted when switching tabs. To retain data don't remove tab from composition tree but
        // make it invisible by e.g. shrinking tab height to 0 dp

//        Column(Modifier.tabDefaults(scaffoldPadding).showIfSelected(MainScreenTab.Postings, selectedTab)) {
//            PostingsTab()
//        }
//        Column(Modifier.tabDefaults(scaffoldPadding).showIfSelected(MainScreenTab.BankAccounts, selectedTab)) {
//            BankAccountsTab()
//        }
        Column(Modifier.tabDefaults(scaffoldPadding).showIfSelected(MainScreenTab.ViewInvoice, selectedTab)) {
            ViewInvoiceTab()
        }
        Column(Modifier.tabDefaults(scaffoldPadding).showIfSelected(MainScreenTab.CreateInvoice, selectedTab)) {
            CreateInvoiceTab()
        }

        val mailService = DI.mailService
        if (mailService != null) {
            Column(Modifier.tabDefaults(scaffoldPadding).showIfSelected(MainScreenTab.Mails, selectedTab)) {
                MailsTab(mailService, uiState.emails)
            }
        }
    }


    StateHandler(uiState)
}

@Composable
private fun Modifier.showIfSelected(tab: MainScreenTab, selectedTab: MainScreenTab) = when (tab == selectedTab) {
    true -> this.fillMaxHeight()
    false -> this.height(0.dp)
}

@Composable
private fun Modifier.tabDefaults(scaffoldPadding: PaddingValues) =
    this.fillMaxWidth().padding(scaffoldPadding).padding(horizontal = 10.dp)