package net.codinux.accounting.domain.persistence

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver

internal actual object AccountingPersistenceNonWeb {

    val jsonDataDir by lazy {
        NSFileManager.defaultManager.URLForDirectory(
            NSApplicationSupportDirectory,
            NSUserDomainMask,
            null,
            true,
            null
        )?.path ?: throw IllegalStateException("Could not resolve Application Support directory")
    }

    actual fun getStorageForJsonDataFiles(): DataStorage = FileSystemDataStorage(jsonDataDir)

    actual fun createSqlDriver(dbName: String, schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver =
        NativeSqliteDriver(schema.synchronous(), dbName)

}