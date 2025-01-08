package net.codinux.accounting.domain

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

internal actual object AccountingPersistenceNonWeb {

    actual fun createSqlDriver(dbName: String, schema: SqlSchema<QueryResult.AsyncValue<Unit>>, version: Long): SqlDriver {
        val dbDir = File(determineDataDirectory(), "db").also { it.mkdirs() }
        val databaseFile = File(dbDir, dbName)

        return JdbcSqliteDriver("jdbc:sqlite:${databaseFile.path}").also { driver ->
            schema.synchronous().also { schema ->
                if (databaseFile.exists() == false) {
                    schema.create(driver)
                }

                schema.migrate(driver, schema.version, version)
            }
        }
    }

    private fun determineDataDirectory(): File {
        return File("./data") // TODO
    }

}