package net.codinux.accounting.ui.appskeleton

import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.codinux.accounting.resources.*
import net.codinux.accounting.resources.Res
import net.codinux.accounting.ui.composables.ToolbarButton
import net.codinux.accounting.ui.tabs.MainScreenTab
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BottomToolbar(selectedTab: MainScreenTab) {

    BottomNavigation(Modifier.height(56.dp)) {
//        ToolbarButton(MainScreenTab.Postings, selectedTab, Icons.Outlined.Book, Res.string.postings)
//
//        ToolbarButton(MainScreenTab.BankAccounts, selectedTab, Icons.Outlined.AccountBalance, Res.string.bank_accounts)

        ToolbarButton(MainScreenTab.ViewInvoice, selectedTab, Icons.AutoMirrored.Outlined.ReceiptLong, Res.string.view_invoice)

        ToolbarButton(MainScreenTab.CreateInvoice, selectedTab, vectorResource(Res.drawable.contract_edit), Res.string.create_invoice)

        ToolbarButton(MainScreenTab.Mails, selectedTab, Icons.Outlined.Email, Res.string.mails)
    }

}