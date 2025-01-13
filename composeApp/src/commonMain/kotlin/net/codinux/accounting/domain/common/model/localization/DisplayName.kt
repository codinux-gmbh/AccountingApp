package net.codinux.accounting.domain.common.model.localization

open class DisplayName<T>(
    val value: T,
    val displayName: String,
    val shortName: String
) {
    override fun toString() = "$displayName $value"
}