package net.codinux.accounting.domain.common.extensions

import io.github.vinceglb.filekit.core.PlatformFile

val PlatformFile.extension: String
    get() = this.name.substringAfterLast('.').lowercase()