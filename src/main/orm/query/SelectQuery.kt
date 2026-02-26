package query

import schema.Table
import schema.Column
import execution.QueryExecutor
import execution.ResultRow

class SelectQuery<T : Table>(
    private val table: T,
    private val executor: QueryExecutor
) {
    private val selectedColumns = mutableListOf<Column<*>>()
    private val conditions = mutableListOf<Condition>()
    private val orderByExpressions = mutableListOf<OrderByExpression>()
    private val groupByExpressions = mutableListOf<GroupByExpression>()
    private var limitValue: Int? = null
    private var offsetValue: Int? = null
    private var distinctFlag: Boolean = false

    fun select(vararg columns: Column<*>): SelectQuery<T> {
        selectedColumns.clear()
        selectedColumns.addAll(columns)
        return this
    }

    fun where(builder: WhereBuilder.() -> Condition): SelectQuery<T> {
        val condition = WhereBuilder().builder()
        conditions.add(condition)
        return this
    }

    fun andWhere(builder: WhereBuilder.() -> Condition): SelectQuery<T> {
        val condition = WhereBuilder().builder()
        if (conditions.isEmpty()) {
            conditions.add(condition)
        } else {
            val existing = conditions.removeAt(conditions.size - 1)
            conditions.add(AndCondition(existing, condition))
        }
        return this
    }

    fun orWhere(builder: WhereBuilder.() -> Condition): SelectQuery<T> {
        val condition = WhereBuilder().builder()
        if (conditions.isEmpty()) {
            conditions.add(condition)
        } else {
            val existing = conditions.removeAt(conditions.size - 1)
            conditions.add(OrCondition(existing, condition))
        }
        return this
    }

    fun orderBy(vararg expressions: OrderByExpression): SelectQuery<T> {
        orderByExpressions.addAll(expressions)
        return this
    }

    fun groupBy(vararg columns: Column<*>): SelectQuery<T> {
        groupByExpressions.addAll(columns.map { GroupByExpression(it) })
        return this
    }

    fun limit(n: Int): SelectQuery<T> {
        require(n > 0) { "LIMIT must be positive" }
        limitValue = n
        return this
    }

    fun offset(n: Int): SelectQuery<T> {
        require(n >= 0) { "OFFSET must be non-negative" }
        offsetValue = n
        return this
    }

    fun distinct(): SelectQuery<T> {
        distinctFlag = true
        return this
    }

    fun execute(): List<ResultRow> {
        val (sql, params) = buildSql()
        return executor.executeQuery(sql, params)
    }

    fun executeSingle(): ResultRow? {
        val (sql, params) = buildSql()
        return executor.executeQuery(sql, params).firstOrNull()
    }

    fun count(): Long {
        val countQuery = "SELECT COUNT(*) FROM ${table.tableName}" +
                if (conditions.isNotEmpty()) " WHERE ${buildWhereClause()}" else ""
        val params = conditions.flatMap { it.getParameters() }
        return executor.executeCount(countQuery, params)
    }

    fun buildSql(): Pair<String, List<Any>> {
        val sql = buildString {
            append("SELECT ")
            if (distinctFlag) append("DISTINCT ")

            if (selectedColumns.isEmpty()) {
                append("*")
            } else {
                append(selectedColumns.joinToString(", ") { it.name })
            }

            append(" FROM ${table.tableName}")

            if (conditions.isNotEmpty()) {
                append(" WHERE ")
                append(buildWhereClause())
            }

            if (groupByExpressions.isNotEmpty()) {
                append(" GROUP BY ")
                append(groupByExpressions.joinToString(", ") { it.toSql() })
            }

            if (orderByExpressions.isNotEmpty()) {
                append(" ORDER BY ")
                append(orderByExpressions.joinToString(", ") { it.toSql() })
            }

            if (limitValue != null) {
                append(" LIMIT $limitValue")
            }

            if (offsetValue != null) {
                append(" OFFSET $offsetValue")
            }
        }

        val params = conditions.flatMap { it.getParameters() }
        return sql to params
    }

    private fun buildWhereClause(): String {
        return conditions.joinToString(" AND ") { it.toSql() }
    }
}