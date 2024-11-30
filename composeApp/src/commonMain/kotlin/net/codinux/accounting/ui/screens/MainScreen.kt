package net.codinux.accounting.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.appskeleton.BottomToolbar
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
        floatingActionButton = if (selectedTab != MainScreenTab.Mails) { { } } else { {
            Column {
                FloatingActionButton(
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = CircleShape,
                    onClick = { uiState.emails.showAddMailAccountDialog.value = true }
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Zeigt ein Menü zum Hinzufügen eines E-Mail Kontos, ... an")
                }

                FloatingActionButton(
                    shape = CircleShape,
                    onClick = { uiState.emails.showOnlyEmailsWithInvoices.value = !uiState.emails.showOnlyEmailsWithInvoices.value }
                ) {
                    Icon(Icons.Outlined.FilterAlt, contentDescription = "Zeigt ein Menü zum Hinzufügen eines E-Mail Kontos, ... an")
                }
            }
        } },
    ) { scaffoldPadding -> // scaffoldPadding contains e.g. the size of the bottom toolbar

        // when removing tabs from composition tree, than tab's state, e.g. entered data, gets
        // deleted when switching tabs. To retain data don't remove tab from composition tree but
        // make it invisible by e.g. shrinking tab height to 0 dp

        Column(Modifier.tabDefaults(scaffoldPadding).showIfSelected(MainScreenTab.Postings, selectedTab)) {
            PostingsTab()
        }
        Column(Modifier.tabDefaults(scaffoldPadding).showIfSelected(MainScreenTab.BankAccounts, selectedTab)) {
            BankAccountsTab()
        }
        Column(Modifier.tabDefaults(scaffoldPadding).showIfSelected(MainScreenTab.Invoices, selectedTab)) {
            InvoicesTab(uiState)
        }
        Column(Modifier.tabDefaults(scaffoldPadding).showIfSelected(MainScreenTab.Mails, selectedTab)) {
            MailsTab(uiState)
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