package net.codinux.accounting.domain.persistence

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver

class SqliteHelper {

    private val singleLongResultMapper = { cursor: SqlCursor ->
        QueryResult.Value(if (cursor.next().value) cursor.getLong(0) else null)
    }


    fun queryLong(driver: SqlDriver, sql: String): Long? =
        driver.executeQuery(null, sql, singleLongResultMapper, 0, null).value

    fun execute(driver: SqlDriver, sql: String) {
        driver.execute(null, sql, 0)
    }


    fun getSchemaVersion(driver: SqlDriver): Long? =
        queryLong(driver, "PRAGMA schema_version")

    fun setSchemaVersion(driver: SqlDriver, newSchemaVersion: Long) {
        execute(driver, "PRAGMA schema_version=$newSchemaVersion")
    }

    fun getUserVersion(driver: SqlDriver): Long? =
        queryLong(driver, "PRAGMA user_version")

    fun setUserVersion(driver: SqlDriver, newUserVersion: Long) {
        execute(driver, "PRAGMA user_version=$newUserVersion")
    }


    fun doesColumnExist(driver: SqlDriver, tableName: String, columnName: String): Boolean =
        queryLong(driver, "SELECT COUNT(*) FROM pragma_table_info('$tableName') where name=\"$columnName\"").let {
            it != null && it > 0
        }

    fun addColumn(driver: SqlDriver, tableName: String, columnName: String) {
        driver.execute(null, "ALTER TABLE $tableName ADD COLUMN $columnName", 0)
    }

    fun renameColumn(driver: SqlDriver, tableName: String, oldColumnName: String, newColumnName: String) {
        execute(driver, "ALTER TABLE $tableName RENAME COLUMN $oldColumnName TO $newColumnName")
    }

}