package query
import schema.Column

sealed class Expression

data class OrderByExpression(
    val column: Column<*>,
    val direction: Direction = Direction.ASC
) : Expression() {
    enum class Direction { ASC, DESC }

    fun toSql() = "${column.name} ${direction.name}"
}

data class GroupByExpression(val column: Column<*>) : Expression() {
    fun toSql() = column.name
}

fun Column<*>.asc() = OrderByExpression(this, OrderByExpression.Direction.ASC)
fun Column<*>.desc() = OrderByExpression(this, OrderByExpression.Direction.DESC)