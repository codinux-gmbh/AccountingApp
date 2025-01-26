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
import net.codinux.accounting.ui.state.ScreenSizeInfo
import net.codinux.accounting.ui.state.UiState
import net.codinux.log.logger

class UiService(
    private val uiState: UiState,
    private val repository: UiStateRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IoOrDefault)
) {

    private val defaultUiState = UiStateEntity(uiState.selectedMainScreenTab.value)

    private var currentUiState = defaultUiState

    private var uiStateInitialized = false

    private val log by logger()


    suspend fun init() {
        try {
            this.currentUiState = loadUiState()

            uiState.selectedMainScreenTab.value = currentUiState.selectedTab
        } catch (e: Throwable) {
            log.error(e) { "Could not initialize persisted UiState" }

            uiState.errorOccurred(ErroneousAction.LoadFromDatabase, Res.string.error_message_could_not_load_ui_state, e)
        }

        this.uiStateInitialized = true
    }


    fun selectedMainScreenTabChanged(selectedTab: MainScreenTab) {
        uiState.selectedMainScreenTab.value = selectedTab

        saveUiState(currentUiState.copy(selectedTab = selectedTab))
    }


    fun windowSizeChanged(screenSize: ScreenSizeInfo) {
        uiState.screenSize.value = screenSize
        uiState.uiType.value = screenSize.uiType

        if (uiStateInitialized) { // don't save initializing window size state, e.g. on desktop window size is first (0, 0) then (1000, 804)
            saveUiState(currentUiState.copy(windowWidth = toInt(screenSize.widthDp), windowHeight = toInt(screenSize.heightDp)))
        }
    }

    fun windowPositionChanged(x: Dp, y: Dp) {
        if (uiStateInitialized) { // don't save initializing window size state, e.g. on desktop window first is centered on screen
            saveUiState(currentUiState.copy(windowPositionX = toInt(x), windowPositionY = toInt(y)))
        }
    }

    private fun toInt(dp: Dp): Int? =
        if (dp.isSpecified) dp.value.toInt()
        else null


    // errors handled by init()
    private suspend fun loadUiState(): UiStateEntity {
        return repository.loadUiState()
            ?: UiStateEntity(MainScreenTab.ViewInvoice)
    }

    private fun saveUiState(newState: UiStateEntity) {
        currentUiState = newState

        coroutineScope.launch {
            try {
                repository.saveUiState(newState)
            } catch (e: Throwable) {
                log.error(e) { "Could not persist UiState" }

                // don't show an error message in this case to user, it's not important enough
//            uiState.errorOccurred(ErroneousAction.SaveToDatabase, Res.string.error_message_could_not_persist_create_invoice_settings, e)
            }
        }
    }
}