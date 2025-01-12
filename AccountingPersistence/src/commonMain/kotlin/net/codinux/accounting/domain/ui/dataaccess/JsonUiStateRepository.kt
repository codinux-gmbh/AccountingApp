package net.codinux.accounting.domain.ui.dataaccess

import net.codinux.accounting.domain.serialization.DataStorage
import net.codinux.accounting.domain.serialization.JsonSerializer
import net.codinux.accounting.domain.ui.model.UiStateEntity

class JsonUiStateRepository(private val serializer: JsonSerializer, private val dateStorage: DataStorage) : UiStateRepository {

    companion object {
        private const val UiStateStorageKey = "UiState"
    }


    override suspend fun loadUiState(): UiStateEntity? =
        dateStorage.get(UiStateStorageKey)?.let { serializer.decode(it) }

    override suspend fun saveUiState(state: UiStateEntity) {
        dateStorage.store(UiStateStorageKey, serializer.encode(state))
    }

}