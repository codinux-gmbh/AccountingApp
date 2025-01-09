package net.codinux.accounting.domain.serialization

interface DataStorage {

    fun store(key: String, value: String)

    fun get(key: String): String?

}