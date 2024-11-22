package net.codinux.accounting.ui.appskeleton

import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.composables.ToolbarButton
import net.codinux.accounting.ui.tabs.MainScreenTab

@Composable
fun BottomToolbar(selectedTab: MainScreenTab) {

    BottomNavigation(Modifier.height(56.dp)) {
        ToolbarButton(MainScreenTab.Postings, selectedTab, Icons.Outlined.Book, "Buchung")

        ToolbarButton(MainScreenTab.BankAccounts, selectedTab, Icons.Outlined.AccountBalance, "Konto")

        ToolbarButton(MainScreenTab.Invoices, selectedTab, Icons.AutoMirrored.Outlined.ReceiptLong, "Rechnung")

        ToolbarButton(MainScreenTab.Mails, selectedTab, Icons.Outlined.Email, "E-Mail")
    }

}