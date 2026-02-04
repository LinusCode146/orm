package query

import schema.Column

sealed class Condition {
    abstract fun toSql(): String
    abstract fun getParameters(): List<Any>
}

data class EqCondition<T>(val column: Column<T>, val value: T) : Condition() {
    override fun toSql() = "${column.name} = ?"
    override fun getParameters() = listOf(value as Any)
}

data class NotEqCondition<T>(val column: Column<T>, val value: T) : Condition() {
    override fun toSql() = "${column.name} != ?"
    override fun getParameters() = listOf(value as Any)
}

data class GreaterThanCondition<T>(val column: Column<T>, val value: T) : Condition() {
    override fun toSql() = "${column.name} > ?"
    override fun getParameters() = listOf(value as Any)
}

data class GreaterThanOrEqCondition<T>(val column: Column<T>, val value: T) : Condition() {
    override fun toSql() = "${column.name} >= ?"
    override fun getParameters() = listOf(value as Any)
}

data class LessThanCondition<T>(val column: Column<T>, val value: T) : Condition() {
    override fun toSql() = "${column.name} < ?"
    override fun getParameters() = listOf(value as Any)
}

data class LessThanOrEqCondition<T>(val column: Column<T>, val value: T) : Condition() {
    override fun toSql() = "${column.name} <= ?"
    override fun getParameters() = listOf(value as Any)
}

data class LikeCondition(val column: Column<String>, val pattern: String) : Condition() {
    override fun toSql() = "${column.name} LIKE ?"
    override fun getParameters() = listOf(pattern)
}

data class NotLikeCondition(val column: Column<String>, val pattern: String) : Condition() {
    override fun toSql() = "${column.name} NOT LIKE ?"
    override fun getParameters() = listOf(pattern)
}

data class InCondition<T>(val column: Column<T>, val values: List<T>) : Condition() {
    override fun toSql() = "${column.name} IN (${values.joinToString(",") { "?" }})"
    override fun getParameters() = values.map { it as Any }
}

data class NotInCondition<T>(val column: Column<T>, val values: List<T>) : Condition() {
    override fun toSql() = "${column.name} NOT IN (${values.joinToString(",") { "?" }})"
    override fun getParameters() = values.map { it as Any }
}

data class IsNullCondition(val column: Column<*>) : Condition() {
    override fun toSql() = "${column.name} IS NULL"
    override fun getParameters() = emptyList<Any>()
}

data class IsNotNullCondition(val column: Column<*>) : Condition() {
    override fun toSql() = "${column.name} IS NOT NULL"
    override fun getParameters() = emptyList<Any>()
}

data class BetweenCondition<T>(val column: Column<T>, val start: T, val end: T) : Condition() {
    override fun toSql() = "${column.name} BETWEEN ? AND ?"
    override fun getParameters() = listOf(start as Any, end as Any)
}

data class AndCondition(val left: Condition, val right: Condition) : Condition() {
    override fun toSql() = "(${left.toSql()} AND ${right.toSql()})"
    override fun getParameters() = left.getParameters() + right.getParameters()
}

data class OrCondition(val left: Condition, val right: Condition) : Condition() {
    override fun toSql() = "(${left.toSql()} OR ${right.toSql()})"
    override fun getParameters() = left.getParameters() + right.getParameters()
}

data class NotCondition(val condition: Condition) : Condition() {
    override fun toSql() = "NOT (${condition.toSql()})"
    override fun getParameters() = condition.getParameters()
}

data class RawCondition(val sql: String, val params: List<Any> = emptyList()) : Condition() {
    override fun toSql() = sql
    override fun getParameters() = params
}
