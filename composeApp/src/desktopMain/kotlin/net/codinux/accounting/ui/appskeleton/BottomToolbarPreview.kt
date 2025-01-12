package net.codinux.accounting.ui.appskeleton

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import net.codinux.accounting.domain.ui.model.MainScreenTab

@Preview
@Composable
fun BottomToolbarPreview() {
    BottomToolbar(MainScreenTab.Postings)
}