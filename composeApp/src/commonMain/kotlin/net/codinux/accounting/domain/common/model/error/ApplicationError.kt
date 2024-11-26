package net.codinux.accounting.domain.common.model.error

import org.jetbrains.compose.resources.StringResource

data class ApplicationError(
    val erroneousAction: ErroneousAction,
    val errorMessage: StringResource,
    val exception: Throwable? = null
) {
    override fun toString() = "$erroneousAction $errorMessage"
}