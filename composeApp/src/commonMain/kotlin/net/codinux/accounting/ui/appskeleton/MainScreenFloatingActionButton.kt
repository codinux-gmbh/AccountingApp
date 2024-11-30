package net.codinux.accounting.ui.appskeleton

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.state.UiState
import net.codinux.accounting.ui.tabs.MainScreenTab

@Composable
fun MainScreenFloatingActionButton(uiState: UiState, selectedTab: MainScreenTab) {
    if (selectedTab == MainScreenTab.Mails) {
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
    }
}