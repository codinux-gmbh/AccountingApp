package net.codinux.accounting.domain.persistence

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.*
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import net.codinux.accounting.domain.serialization.DataStorage
import net.codinux.accounting.domain.serialization.FileSystemDataStorage
import net.codinux.log.logger
import java.io.File

internal actual object AccountingPersistenceNonWeb {

    private val sqliteHelper: SqliteHelper = SqliteHelper()

    private val log by logger()


    actual fun getStorageForJsonDataFiles(): DataStorage = FileSystemDataStorage(JvmPersistence.jsonDataDirectory)

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

            val currentVersion = sqliteHelper.getUserVersion(driver) ?: 0L

            log.debug { "DB: currentVersion = $currentVersion, newVersion = $newVersion" }

            if (currentVersion > 1) {
                schema.migrate(driver, currentVersion, newVersion)
            }

            if (currentVersion < newVersion) {
                sqliteHelper.setUserVersion(driver, newVersion)
            }
        } catch (e: Throwable) {
            log.error(e) { "Migrating database failed" }
        }
    }

}