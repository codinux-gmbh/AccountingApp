package net.codinux.accounting.ui.state

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import net.codinux.accounting.ui.tabs.MainScreenTab

class UiState : ViewModel() {

    val selectedMainScreenTab = MutableStateFlow(MainScreenTab.Postings)

}