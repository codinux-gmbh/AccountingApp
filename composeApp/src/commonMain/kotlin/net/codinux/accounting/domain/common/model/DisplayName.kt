package net.codinux.accounting.domain.common.model

class DisplayName<T>(
    val value: T,
    val displayName: String
) {
    override fun toString() = "$displayName $value"
}