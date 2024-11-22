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

@Composable
fun BottomToolbar() {

    BottomNavigation(Modifier.height(56.dp)) {
        ToolbarButton(Icons.Outlined.Book, "Buchung") { }

        ToolbarButton(Icons.Outlined.AccountBalance, "Konto") { }

        ToolbarButton(Icons.AutoMirrored.Outlined.ReceiptLong, "Rechnung") { }

        ToolbarButton(Icons.Outlined.Email, "E-Mail") { }
    }

}