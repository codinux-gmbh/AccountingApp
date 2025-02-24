package net.codinux.accounting.domain.serialization

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.refTo
import platform.Foundation.*

open class FileSystemDataStorage(protected val storageDirectory: NSURL) : DataStorage {

    override fun store(key: String, value: String) {
        writeToFile(storageDirectory, key, value)
    }

    override fun get(key: String): String? {
        val filePath = getFileForKey(key)

        val data = NSData.dataWithContentsOfFile(filePath)
            ?: throw IOException("File not found: $filePath")

        val content = NSString.create(data, NSUTF8StringEncoding)
            ?: throw IOException("Failed to decode file: $filePath")

        return content as String
    }


    open fun writeToFile(directory: NSURL, filename: String, fileContent: String) {
        val file = getFileInDirectory(directory, filename)

        writeTextToFile(file, fileContent)
    }

    open fun writeToFile(directory: NSURL, filename: String, fileContent: ByteArray) {
        val file = getFileInDirectory(directory, filename)

        writeBinaryDataToFile(file, fileContent)
    }

    open fun writeTextToFile(filePath: NSURL, fileContent: String) =
        writeBinaryDataToFile(filePath, fileContent.encodeToByteArray())

    @OptIn(ExperimentalForeignApi::class)
    open fun writeBinaryDataToFile(filePath: NSURL, fileContent: ByteArray?) {
        val nsData = fileContent.toNSData()

        nsData.writeToURL(filePath, true)
    }

    protected open fun getFileForKey(key: String): NSURL = getFileInDirectory(storageDirectory, key)

    protected open fun getFileInDirectory(directory: NSURL, filename: String): NSURL {
        val fileComponents = directory?.pathComponents?.plus(filename)
            ?: throw IllegalStateException("Failed to get get file with name '$filename' in directory '$directory'")

        return NSURL.fileURLWithPathComponents(fileComponents)
            ?: throw IllegalStateException("Failed to create file URL for file with name '$filename' in directory '$directory'")
    }

}

fun ByteArray?.toNSData(): NSData =
    if (this == null) NSData()
    else NSData.dataWithBytes(this.refTo(0), this.size.toULong())

// alternatively:
/*
fun ByteArray?.toNSData(): NSData = memScoped {
    if (this == null) NSData()
    else {
        NSData.dataWithBytes(
            bytes = this.refTo(0).getPointer(this),
            length = this.size.toULong()
        )
    }
 */