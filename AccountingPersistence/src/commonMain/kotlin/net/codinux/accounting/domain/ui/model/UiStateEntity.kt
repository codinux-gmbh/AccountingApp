package net.codinux.accounting.domain.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class UiStateEntity(
    val selectedTab: MainScreenTab,

    val windowPositionX: Int? = null,
    val windowPositionY: Int? = null,
    val windowWidth: Int? = null,
    val windowHeight: Int? = null,
    val windowState: String? = null
)