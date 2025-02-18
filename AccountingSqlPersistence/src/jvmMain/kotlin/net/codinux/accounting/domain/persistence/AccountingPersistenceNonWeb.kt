package net.codinux.accounting.domain.persistence

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.*
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import net.codinux.log.logger
import java.io.File

internal actual object AccountingPersistenceNonWeb {

    private val log by logger()


    actual fun createSqlDriver(dbName: String, schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver {
        val dbDir = JvmPersistence.databaseDirectory
        val databaseFile = File(dbDir, dbName)

        return JdbcSqliteDriver("jdbc:sqlite:${databaseFile.path}").also { driver ->
            schema.synchronous().also { schema ->
                if (databaseFile.exists() == false) {
                    schema.create(driver)
                }

                migrateToNewVersion(schema, driver)
            }
        }
    }

    private fun migrateToNewVersion(schema: SqlSchema<QueryResult.Value<Unit>>, driver: JdbcSqliteDriver) {
        try {
            val newVersion = schema.version

            val mapper = { cursor: SqlCursor ->
                QueryResult.Value(if (cursor.next().value) cursor.getLong(0) else null)
            }

            val currentVersion = driver.executeQuery(null, "PRAGMA user_version", mapper, 0, null).value ?: 0L

            log.debug { "DB: currentVersion = $currentVersion, newVersion = $newVersion" }

            if (currentVersion > 1) {
                schema.migrate(driver, currentVersion, newVersion)
            }

            if (currentVersion < newVersion) {
                driver.execute(null, "PRAGMA user_version=$newVersion", 0, null)
            }
        } catch (e: Throwable) {
            log.error(e) { "Migrating database failed" }
        }
    }

}