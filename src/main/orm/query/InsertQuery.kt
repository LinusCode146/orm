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

    fun execute(): Int {
        val (sql, params) = buildSql()
        return executor.executeUpdate(sql, params)
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
