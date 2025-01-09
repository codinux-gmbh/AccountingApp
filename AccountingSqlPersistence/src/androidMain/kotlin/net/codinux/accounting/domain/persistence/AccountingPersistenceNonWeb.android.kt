package net.codinux.accounting.domain.persistence

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import net.codinux.kotlin.android.AndroidContext

internal actual object AccountingPersistenceNonWeb {

    actual fun createSqlDriver(dbName: String, schema: SqlSchema<QueryResult.AsyncValue<Unit>>, version: Long): SqlDriver =
        AndroidSqliteDriver(schema.synchronous(), AndroidContext.applicationContext, dbName)

}