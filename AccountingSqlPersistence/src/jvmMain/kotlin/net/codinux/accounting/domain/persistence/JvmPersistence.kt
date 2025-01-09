package net.codinux.accounting.domain.persistence

import net.harawata.appdirs.AppDirsFactory
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.name

object JvmPersistence {

    private val applicationDataDirectory = determineDataDirectory()

    val invoicesDirectory = ensureDirectory(applicationDataDirectory, "invoices")

    val databaseDirectory = ensureDirectory(applicationDataDirectory, "db")


    // TODO: move to common JVM and Android code
    private fun ensureDirectory(parentDir: File, directoryName: String): File = File(parentDir, directoryName).also {
        it.mkdirs()
    }



    private fun determineDataDirectory(): File {
        val currentDir = Path(System.getProperty("user.dir"))

        // if the current directory is writable, use that one (the default for development)
        val dataDir = if (Files.isWritable(currentDir)) { // couldn't believe it, but java.io.File returned folder is writable for "C:\\Program Files\\"
            File(currentDir.absolutePathString(), "data")
        } else { // otherwise use eInvoicing dir in user's home dir (the default for releases)
            val appDirs = AppDirsFactory.getInstance()
            val unwantedVersionDir = "unwanted_version_dir"
            val userDataDir = Path(appDirs.getUserDataDir("eInvoicing", unwantedVersionDir, "codinux"))
            if (userDataDir.name == unwantedVersionDir) userDataDir.parent.toFile()
            else userDataDir.toFile()
        }

        return dataDir.also { it.mkdirs() }
    }

}