package net.codinux.accounting.domain.persistence

import net.codinux.log.logger
import kotlin.enums.EnumEntries
import kotlin.jvm.JvmName

class SqldelightMapper {

    private val log by logger()


    fun <E : Enum<E>> mapEnum(enum: Enum<E>): String = enum.name

    fun <E : Enum<E>> mapToEnum(enumName: String, values: EnumEntries<E>): E =
        try {
            values.first { it.name == enumName }
        } catch (e: Throwable) {
            log.error(e) { "Could not map enumName '$enumName' to ${values.first()::class}"}
            throw e
        }


    fun <E : Enum<E>> mapToEnum(enumName: String, values: EnumEntries<E>, enumNamesToMigrate: Map<String, String>): E =
        mapToEnum(enumNamesToMigrate[enumName] ?: enumName, values)

    fun <E : Enum<E>> mapToEnumNullable(enumName: String, values: EnumEntries<E>): E? {
        val mapped = values.firstOrNull { it.name == enumName }

        if (mapped == null) {
            log.warn("Could not map '$enumName' to Enum ${values.first()::class}")
        }

        return mapped
    }


    @JvmName("mapIntNullable")
    fun mapInt(int: Int?): Long? =
        int?.let { mapInt(it) }

    fun mapInt(int: Int): Long = int.toLong()

    @JvmName("mapToIntNullable")
    fun mapToInt(int: Long?): Int? =
        int?.let { mapToInt(it) }

    fun mapToInt(int: Long): Int = int.toInt()

}