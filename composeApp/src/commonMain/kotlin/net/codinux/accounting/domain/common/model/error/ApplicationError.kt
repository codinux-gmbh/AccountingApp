package net.codinux.accounting.domain.common.model.error

data class ApplicationError(
    val erroneousAction: ErroneousAction,
    val errorMessage: String,
    val exception: Throwable? = null
) {
    override fun toString() = "$erroneousAction $errorMessage"
}