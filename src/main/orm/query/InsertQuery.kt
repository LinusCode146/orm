package query

import schema.Table
import schema.Column
import execution.QueryExecutor
import execution.ResultRow

class InsertQuery<T : Table>(
    private val table: T,
    private val executor: QueryExecutor
) {
    private val valueSets = mutableListOf<Map<Column<*>, Any?>>()
    private val returningColumns = mutableListOf<Column<*>>()

    fun values(builder: InsertBuilder<T>.() -> Unit): InsertQuery<T> {
        val insertBuilder = InsertBuilder(table)
        insertBuilder.builder()
        valueSets.add(insertBuilder.values)
        return this
    }

    fun values(vararg builders: InsertBuilder<T>.() -> Unit): InsertQuery<T> {
        builders.forEach { builder ->
            val insertBuilder = InsertBuilder(table)
            insertBuilder.builder()
            valueSets.add(insertBuilder.values)
        }
        return this
    }

    fun returning(vararg columns: Column<*>): InsertQuery<T> {
        returningColumns.addAll(columns)
        return this
    }

    fun execute(): Int {
        val (sql, params) = buildSql()
        return executor.executeUpdate(sql, params)
    }

    fun executeSingle(): ResultRow? {
        require(returningColumns.isNotEmpty()) {
            "executeSingle() requires RETURNING clause"
        }
        val (sql, params) = buildSql()
        return executor.executeQuery(sql, params).firstOrNull()
    }

    private fun buildSql(): Pair<String, List<Any?>> {
        require(valueSets.isNotEmpty()) { "No values specified for INSERT" }

        val firstSet = valueSets.first()
        val columns = firstSet.keys.toList()

        val sql = buildString {
            append("INSERT INTO ${table.tableName} ")
            append("(${columns.joinToString(", ") { it.name }})")
            append(" VALUES ")

            append(valueSets.joinToString(", ") { valueSet ->
                "(${columns.joinToString(", ") { "?" }})"
            })

            if (returningColumns.isNotEmpty()) {
                append(" RETURNING ")
                append(returningColumns.joinToString(", ") { it.name })
            }
        }

        val params = valueSets.flatMap { valueSet ->
            columns.map { column -> valueSet[column] }
        }

        return (sql to params)
    }
}

class InsertBuilder<T : Table>(private val table: T) {
    internal val values = mutableMapOf<Column<*>, Any?>()

    operator fun <V> Column<V>.invoke(value: V) {
        values[this] = value
    }

    // Alternative syntax
    infix fun <V> Column<V>.setValue(value: V) {
        values[this] = value
    }
}