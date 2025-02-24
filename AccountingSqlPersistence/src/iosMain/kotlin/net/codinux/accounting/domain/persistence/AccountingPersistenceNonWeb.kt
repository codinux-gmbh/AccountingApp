package net.codinux.accounting.domain.persistence

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import net.codinux.accounting.domain.serialization.DataStorage
import net.codinux.accounting.domain.serialization.FileSystemDataStorage
import platform.Foundation.*

internal actual object AccountingPersistenceNonWeb {

    @OptIn(ExperimentalForeignApi::class)
    val jsonDataDir by lazy {
        NSFileManager.defaultManager.URLForDirectory(
            NSApplicationSupportDirectory,
            NSUserDomainMask,
            null,
            true,
            null
        ) ?: throw IllegalStateException("Could not resolve Application Support directory")
    }

    actual fun getStorageForJsonDataFiles(): DataStorage = FileSystemDataStorage(jsonDataDir)

    actual fun createSqlDriver(dbName: String, schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver =
        NativeSqliteDriver(schema.synchronous(), dbName)

}