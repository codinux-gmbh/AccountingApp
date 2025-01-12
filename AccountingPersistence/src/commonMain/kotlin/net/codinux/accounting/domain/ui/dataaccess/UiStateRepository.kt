package net.codinux.accounting.domain.ui.dataaccess

import net.codinux.accounting.domain.ui.model.UiStateEntity

interface UiStateRepository {

    suspend fun loadUiState(): UiStateEntity?

    suspend fun saveUiState(state: UiStateEntity)

}