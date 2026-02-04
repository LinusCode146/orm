package query

import schema.Table
import schema.Column
import execution.QueryExecutor

class UpdateQuery<T : Table>(
    private val table: T,
    private val executor: QueryExecutor
) {
    private val updates = mutableMapOf<Column<*>, Any?>()
    private val conditions = mutableListOf<Condition>()

    fun set(builder: UpdateBuilder<T>.() -> Unit): UpdateQuery<T> {
        val updateBuilder = UpdateBuilder(table)
        updateBuilder.builder()
        updates.putAll(updateBuilder.updates)
        return this
    }

    fun where(builder: WhereBuilder.() -> Condition): UpdateQuery<T> {
        val condition = WhereBuilder().builder()
        conditions.add(condition)
        return this
    }

    fun execute(): Int {
        require(updates.isNotEmpty()) { "No updates specified" }
        val (sql, params) = buildSql()
        return executor.executeUpdate(sql, params)
    }

    private fun buildSql(): Pair<String, List<Any?>> {
        val sql = buildString {
            append("UPDATE ${table.tableName} SET ")
            append(updates.keys.joinToString(", ") { "${it.name} = ?" })

            if (conditions.isNotEmpty()) {
                append(" WHERE ")
                append(conditions.joinToString(" AND ") { it.toSql() })
            }
        }

        val params = updates.values.toList() +
                conditions.flatMap { it.getParameters() }

        return sql to params
    }
}

class UpdateBuilder<T : Table>(private val table: T) {
    internal val updates = mutableMapOf<Column<*>, Any?>()

    operator fun <V> Column<V>.invoke(value: V) {
        updates[this] = value
    }

    infix fun <V> Column<V>.setValue(value: V) {
        updates[this] = value
    }
}