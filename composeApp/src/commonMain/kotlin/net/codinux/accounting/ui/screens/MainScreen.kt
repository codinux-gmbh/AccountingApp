package net.codinux.accounting.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
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

    val isKeyboardVisible = uiState.isKeyboardVisible.collectAsState().value


    Scaffold(
        bottomBar = { BottomToolbar(selectedTab) },
        backgroundColor = Colors.MainBackgroundColor,
        floatingActionButton = { MainScreenFloatingActionButton(uiState, selectedTab) },
    ) { scaffoldPadding -> // scaffoldPadding contains e.g. the size of the bottom toolbar

        Column(Modifier.fillMaxSize().paddingForKeyboardState(isKeyboardVisible, scaffoldPadding).padding(horizontal = 10.dp)) {
            // when removing tabs from composition tree, then tab's state, e.g. entered data, gets
            // deleted when switching tabs. To retain data don't remove tab from composition tree but
            // make it invisible by e.g. shrinking tab height to 0 dp

//        Column(Modifier.tabDefaults(scaffoldPadding).showIfSelected(MainScreenTab.Postings, selectedTab)) {
//            PostingsTab()
//        }
//        Column(Modifier.tabDefaults(scaffoldPadding).showIfSelected(MainScreenTab.BankAccounts, selectedTab)) {
//            BankAccountsTab()
//        }
            Column(Modifier.showIfSelected(MainScreenTab.ViewInvoice, selectedTab)) {
                ViewInvoiceTab()
            }
            Column(Modifier.showIfSelected(MainScreenTab.CreateInvoice, selectedTab)) {
                CreateInvoiceTab()
            }

            val mailService = DI.mailService
            if (mailService != null) {
                Column(Modifier.showIfSelected(MainScreenTab.Mails, selectedTab)) {
                    MailsTab(mailService, uiState.emails)
                }
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
private fun Modifier.paddingForKeyboardState(isKeyboardVisible: Boolean, scaffoldPadding: PaddingValues): Modifier =
    if (isKeyboardVisible) {
        this.imePadding()
    }
    else {
        this.padding(scaffoldPadding) // if we want to display BottomToolbar also when keyboard is visible, apply scaffoldPadding in all states (not only when keyboard is hidden)
    }