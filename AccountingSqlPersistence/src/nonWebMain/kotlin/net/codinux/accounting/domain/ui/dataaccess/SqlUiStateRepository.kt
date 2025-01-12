package net.codinux.accounting.domain.ui.dataaccess

import net.codinux.accounting.domain.persistence.SqldelightMapper
import net.codinux.accounting.domain.ui.model.MainScreenTab
import net.codinux.accounting.domain.ui.model.UiStateEntity
import net.codinux.accounting.persistence.AccountingDb

class SqlUiStateRepository(database: AccountingDb, private val mapper: SqldelightMapper) : UiStateRepository {

    private val queries = database.uiStateQueries


    override suspend fun loadUiState(): UiStateEntity? =
        queries.getUiState { _, selectedTab, windowPositionX, windowPositionY, windowWidth, windowHeight, windowState ->
            UiStateEntity(
                mapper.mapToEnum(selectedTab, MainScreenTab.entries),

                mapper.mapToInt(windowPositionX), mapper.mapToInt(windowPositionY),
                mapper.mapToInt(windowWidth), mapper.mapToInt(windowHeight),
                windowState
            )
        }.executeAsOneOrNull()

    override suspend fun saveUiState(state: UiStateEntity) {
        queries.upsertUiState(
            mapper.mapEnum(state.selectedTab),

            mapper.mapInt(state.windowPositionX), mapper.mapInt(state.windowPositionY),
            mapper.mapInt(state.windowWidth), mapper.mapInt(state.windowHeight),
            state.windowState
        )
    }

}