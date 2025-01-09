package net.codinux.accounting.domain.persistence

internal actual object AccountingPersistenceNonWeb {

    actual fun createSqlDriver(dbName: String, schema: SqlSchema<QueryResult.AsyncValue<Unit>>, version: Long): SqlDriver =
        NativeSqliteDriver(schema.synchronous(), dbName)

}