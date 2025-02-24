package net.codinux.accounting.domain.persistence

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.*
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import net.codinux.accounting.domain.serialization.DataStorage
import net.codinux.accounting.domain.serialization.FileSystemDataStorage
import net.codinux.kotlin.android.AndroidContext

internal actual object AccountingPersistenceNonWeb {

    val jsonDataDir by lazy { AndroidContext.applicationContext.getDir("jsonData", Context.MODE_PRIVATE) }

    // alternatively:
//    val jsonDataDir by lazy { File(AndroidContext.applicationContext.filesDir, "jsonData") }


    actual fun getStorageForJsonDataFiles(): DataStorage = FileSystemDataStorage(jsonDataDir)

    actual fun createSqlDriver(dbName: String, schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver =
        AndroidSqliteDriver(schema.synchronous(), AndroidContext.applicationContext, dbName)

}