package net.codinux.accounting.domain.serialization

class InMemoryDataStorage : DataStorage {

    // TODO: when used in multi-threaded code use a synchronized Map
    private val store = mutableMapOf<String, String>()


    override fun store(key: String, value: String) {
        store[key] = value
    }

    override fun get(key: String): String? =
        store[key]

}