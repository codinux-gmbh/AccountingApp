package net.codinux.accounting.domain.serialization

import kotlinx.browser.localStorage

class LocalStorageDataStorage : DataStorage {

    override fun store(key: String, value: String) {
        localStorage.setItem(key, value)
    }

    override fun get(key: String): String? =
        localStorage.getItem(key)

}