package execution

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

/**
 * Hauptklasse für Query-Ausführung
 * Handhabt PreparedStatements, Parameter-Binding und Result-Mapping
 */
class QueryExecutor(private val connectionProvider: () -> Connection) {

    /**
     * Führt eine SELECT Query aus und gibt ResultRows zurück
     */
    fun executeQuery(sql: String, params: List<Any?>): List<ResultRow> {
        return useConnection { connection ->
            connection.prepareStatement(sql).use { statement ->
                bindParameters(statement, params)

                val resultSet = statement.executeQuery()
                val results = mutableListOf<ResultRow>()

                while (resultSet.next()) {
                    results.add(mapResultSetToRow(resultSet))
                }

                results
            }
        }
    }

    /**
     * Führt eine SELECT Query aus und gibt das erste Result zurück
     */
    fun executeQuerySingle(sql: String, params: List<Any>): ResultRow? {
        return executeQuery(sql, params).firstOrNull()
    }

    /**
     * Führt eine UPDATE/INSERT/DELETE Query aus
     * @return Anzahl der betroffenen Zeilen
     */
    fun executeUpdate(sql: String, params: List<Any?>): Int {
        return useConnection { connection ->
            connection.prepareStatement(sql).use { statement ->
                bindParameters(statement, params)
                statement.executeUpdate()
            }
        }
    }

    /**
     * Führt INSERT mit RETURNING aus (PostgreSQL)
     */
    fun executeInsertReturning(sql: String, params: List<Any>): ResultRow? {
        return useConnection { connection ->
            connection.prepareStatement(sql).use { statement ->
                bindParameters(statement, params)

                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    mapResultSetToRow(resultSet)
                } else {
                    null
                }
            }
        }
    }

    /**
     * Führt INSERT aus und gibt generierte Keys zurück
     */
    fun executeInsertWithGeneratedKeys(sql: String, params: List<Any>): Long? {
        return useConnection { connection ->
            connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { statement ->
                bindParameters(statement, params)
                statement.executeUpdate()

                val generatedKeys = statement.generatedKeys
                if (generatedKeys.next()) {
                    generatedKeys.getLong(1)
                } else {
                    null
                }
            }
        }
    }

    /**
     * Führt COUNT Query aus
     */
    fun executeCount(sql: String, params: List<Any>): Long {
        return useConnection { connection ->
            connection.prepareStatement(sql).use { statement ->
                bindParameters(statement, params)

                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    resultSet.getLong(1)
                } else {
                    0L
                }
            }
        }
    }

    /**
     * Führt Batch Operations aus
     * @return Array mit Anzahl betroffener Zeilen pro Statement
     */
    fun executeBatch(statements: List<Pair<String, List<Any>>>): IntArray {
        return useConnection { connection ->
            connection.autoCommit = false

            try {
                val results = statements.map { (sql, params) ->
                    connection.prepareStatement(sql).use { statement ->
                        bindParameters(statement, params)
                        statement.executeUpdate()
                    }
                }

                connection.commit()
                results.toIntArray()
            } catch (e: Exception) {
                connection.rollback()
                throw e
            } finally {
                connection.autoCommit = true
            }
        }
    }

    /**
     * Bindet Parameter an PreparedStatement
     */
    private fun bindParameters(statement: PreparedStatement, params: List<Any?>) {
        params.forEachIndexed { index, param ->
            val position = index + 1  // JDBC ist 1-indexed

            when (param) {
                is String -> statement.setString(position, param)
                is Int -> statement.setInt(position, param)
                is Long -> statement.setLong(position, param)
                is Double -> statement.setDouble(position, param)
                is Float -> statement.setFloat(position, param)
                is Boolean -> statement.setBoolean(position, param)
                is ByteArray -> statement.setBytes(position, param)
                null -> statement.setNull(position, java.sql.Types.NULL)
                else -> statement.setObject(position, param)
            }
        }
    }

    /**
     * Konvertiert JDBC ResultSet zu ResultRow
     */
    private fun mapResultSetToRow(resultSet: ResultSet): ResultRow {
        val metadata = resultSet.metaData
        val columnCount = metadata.columnCount

        val data = mutableMapOf<String, Any?>()

        for (i in 1..columnCount) {
            val columnName = metadata.getColumnLabel(i)
            val value = resultSet.getObject(i)
            data[columnName] = value
        }

        return ResultRow(data)
    }

    /**
     * Helper für Connection Management
     */
    private fun <T> useConnection(block: (Connection) -> T): T {
        return connectionProvider().use { connection ->
            block(connection)
        }
    }
}