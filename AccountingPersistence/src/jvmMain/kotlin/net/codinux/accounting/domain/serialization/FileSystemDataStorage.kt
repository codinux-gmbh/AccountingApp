package net.codinux.accounting.domain.serialization

import java.io.File

open class FileSystemDataStorage(protected val storageDirectory: File) : DataStorage {

    override fun store(key: String, value: String) {
        getFileForKey(key).writeText(value)
    }

    override fun get(key: String): String? {
        val file = getFileForKey(key)

        return if (file.exists()) file.readText()
                else null
    }


    protected open fun getFileForKey(key: String): File = storageDirectory.resolve(key)

}