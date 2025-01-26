package net.codinux.accounting.domain.ui.dataaccess

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import kotlinx.coroutines.test.runTest
import net.codinux.accounting.domain.persistence.AccountingPersistence
import net.codinux.accounting.domain.serialization.InMemoryDataStorage
import net.codinux.accounting.domain.ui.model.MainScreenTab
import net.codinux.accounting.domain.ui.model.UiStateEntity
import kotlin.test.Test

class JsonUiStateRepositoryTest {

    private val underTest = JsonUiStateRepository(AccountingPersistence.serializer, InMemoryDataStorage())


    @Test
    fun saveAndRetrieveUiState() = runTest {
        val uiState = UiStateEntity(MainScreenTab.CreateInvoice, 7, 8, 850, 600, false, "Maximized")


        underTest.saveUiState(uiState)

        val result = underTest.loadUiState()


        assertThat(result).isNotNull()

        assertThat(result!!.selectedTab).isEqualTo(uiState.selectedTab)

        assertThat(result.windowPositionX).isEqualTo(uiState.windowPositionX)
        assertThat(result.windowPositionY).isEqualTo(uiState.windowPositionY)
        assertThat(result.windowWidth).isEqualTo(uiState.windowWidth)
        assertThat(result.windowHeight).isEqualTo(uiState.windowHeight)
        assertThat(result.windowState).isEqualTo(uiState.windowState)
    }

}