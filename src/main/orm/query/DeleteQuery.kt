package query

import schema.Table
import execution.QueryExecutor

class DeleteQuery<T : Table>(
    private val table: T,
    private val executor: QueryExecutor
) {
    private val conditions = mutableListOf<Condition>()
    private var deleteAll = false

    fun where(builder: WhereBuilder.() -> Condition): DeleteQuery<T> {
        val condition = WhereBuilder().builder()
        conditions.add(condition)
        return this
    }

    fun all(): DeleteQuery<T> {
        deleteAll = true
        return this
    }

    fun execute(): Int {
        if (!deleteAll && conditions.isEmpty()) {
            throw IllegalStateException(
                "DELETE without WHERE is dangerous. Use .all() if you really want to delete everything."
            )
        }

        val (sql, params) = buildSql()
        return executor.executeUpdate(sql, params)
    }

    private fun buildSql(): Pair<String, List<Any>> {
        val sql = buildString {
            append("DELETE FROM ${table.tableName}")

            if (conditions.isNotEmpty()) {
                append(" WHERE ")
                append(conditions.joinToString(" AND ") { it.toSql() })
            }
        }

        val params = conditions.flatMap { it.getParameters() }
        return sql to params
    }
}