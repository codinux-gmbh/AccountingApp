package net.codinux.accounting.domain.common.model.error

import net.codinux.invoicing.model.dto.SerializableException
import org.jetbrains.compose.resources.StringResource

data class ApplicationError(
    val erroneousAction: ErroneousAction,
    val errorMessage: StringResource,
    val exception: SerializableException? = null,
    val errorMessageArguments: Collection<Any> = emptyList()
) {
    override fun toString() = "$erroneousAction $errorMessage"
}