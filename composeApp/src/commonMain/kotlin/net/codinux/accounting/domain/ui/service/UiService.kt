package net.codinux.accounting.domain.ui.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.ui.dataaccess.UiStateRepository
import net.codinux.accounting.domain.ui.model.MainScreenTab
import net.codinux.accounting.domain.ui.model.UiStateEntity
import net.codinux.accounting.platform.IoOrDefault
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.state.UiState
import net.codinux.log.logger

class UiService(
    private val uiState: UiState,
    private val repository: UiStateRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IoOrDefault)
) {

    private val defaultUiState = UiStateEntity(uiState.selectedMainScreenTab.value)

    private var currentUiState = defaultUiState

    private val log by logger()


    suspend fun init() {
        try {
            this.currentUiState = loadUiState()

            uiState.selectedMainScreenTab.value = currentUiState.selectedTab
        } catch (e: Throwable) {
            log.error(e) { "Could not initialize persisted UiState" }

            uiState.errorOccurred(ErroneousAction.LoadFromDatabase, Res.string.error_message_could_not_load_ui_state, e)
        }
    }


    fun selectedMainScreenTabChanged(selectedTab: MainScreenTab) {
        uiState.selectedMainScreenTab.value = selectedTab

        currentUiState = currentUiState.copy(selectedTab = selectedTab)
        saveUiState(currentUiState)
    }


    // errors handled by init()
    private suspend fun loadUiState(): UiStateEntity {
        return repository.loadUiState()
            ?: UiStateEntity(MainScreenTab.ViewInvoice)
    }

    private fun saveUiState(state: UiStateEntity) {
        coroutineScope.launch {
            try {
                repository.saveUiState(state)
            } catch (e: Throwable) {
                log.error(e) { "Could not persist UiState" }

                // don't show an error message in this case to user, it's not important enough
//            uiState.errorOccurred(ErroneousAction.SaveToDatabase, Res.string.error_message_could_not_persist_create_invoice_settings, e)
            }
        }
    }
}