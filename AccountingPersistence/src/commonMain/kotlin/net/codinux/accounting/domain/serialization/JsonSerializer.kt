package net.codinux.accounting.domain.serialization

import kotlinx.serialization.json.Json

class JsonSerializer {

    val json = Json {
        ignoreUnknownKeys = true
    }


    inline fun <reified T : Any> encodeNullable(value: T?): String? =
        value?.let { encode(it) }

    inline fun <reified T : Any> encode(value: T): String =
        json.encodeToString(value)

    inline fun <reified T : Any> decode(json: String): T =
        this.json.decodeFromString(json)

}