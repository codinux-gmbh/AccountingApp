package net.codinux.accounting.domain.serialization

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.refTo
import kotlinx.io.IOException
import platform.Foundation.*

open class FileSystemDataStorage(protected val storageDirectory: NSURL) : DataStorage {

    override fun store(key: String, value: String) {
        writeToFile(storageDirectory, key, value)
    }

    @OptIn(BetaInteropApi::class)
    override fun get(key: String): String? {
        val filePath = getFileForKey(key)

        val data = NSData.dataWithContentsOfURL(filePath)
            ?: return null // it's totally fine if file does not exist (yet)

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
        val fileComponents = directory.pathComponents?.plus(filename)
            ?: throw IllegalStateException("Failed to get get file with name '$filename' in directory '$directory'")

        return NSURL.fileURLWithPathComponents(fileComponents)
            ?: throw IllegalStateException("Failed to create file URL for file with name '$filename' in directory '$directory'")
    }

}

@OptIn(ExperimentalForeignApi::class)
fun ByteArray?.toNSData(): NSData = this.let { bytes ->
    memScoped {
        if (bytes == null) NSData()
        else {
            NSData.dataWithBytes(
                bytes = bytes.refTo(0).getPointer(this),
                length = bytes.size.toULong()
            )
        }
    }
}