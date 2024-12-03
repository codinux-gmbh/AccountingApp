package net.codinux.accounting.ui.composables.mail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import net.codinux.accounting.ui.composables.forms.RoundedCornersCard
import net.codinux.accounting.ui.config.Style
import net.codinux.accounting.ui.state.EmailsUiState

@Composable
fun BoxScope.EmailFilterPanel(uiState: EmailsUiState) {

    val showOnlyEmailsWithInvoices = uiState.showOnlyEmailsWithInvoices.collectAsState().value


    RoundedCornersCard(
        Modifier
            .padding(end = 56.dp + 6.dp) // place to the left of FloatingActionButton (56 = FAB size, 6 = FAB padding to the right screen edge)
            .padding(start = 6.dp, end = 6.dp, bottom = 16.dp)
            .width(56.dp)
            .height(56.dp)
            .align(Alignment.BottomEnd),
        backgroundColor = MaterialTheme.colors.secondary // the same as FloatingActionButtons
    ) {
        Column(Modifier.fillMaxWidth().padding(all = Style.FormCardPadding)) {
            IconToggleButton(showOnlyEmailsWithInvoices, { uiState.showOnlyEmailsWithInvoices.value = it }, Modifier.size(48.dp).backgroundIf(showOnlyEmailsWithInvoices, MaterialTheme.colors.primary, CircleShape)) {
                Icon(Icons.AutoMirrored.Outlined.ReceiptLong, "Show only emails with invoices", tint = MaterialTheme.colors.onPrimary)
            }
        }
    }

}

fun Modifier.backgroundIf(condition: Boolean, color: Color, shape: Shape = RectangleShape) =
    if (condition) this.background(color, shape)
    else this