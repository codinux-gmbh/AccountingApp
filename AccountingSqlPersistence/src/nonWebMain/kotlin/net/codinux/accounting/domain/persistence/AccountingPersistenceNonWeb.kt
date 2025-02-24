package net.codinux.accounting.domain.persistence

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import net.codinux.accounting.domain.serialization.DataStorage

internal expect object AccountingPersistenceNonWeb {

    fun getStorageForJsonDataFiles(): DataStorage

    fun createSqlDriver(dbName: String, schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver

}