package net.codinux.accounting.domain.common.model.localization

class PrioritizedDisplayNames<T>(
    val all: List<DisplayName<T>>,
    val preferredValues: List<DisplayName<T>>,
    val minorValues: List<DisplayName<T>>
)