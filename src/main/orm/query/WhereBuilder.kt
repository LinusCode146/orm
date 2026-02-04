package query

import schema.Column

class WhereBuilder {
    infix fun <T> Column<T>.eq(value: T) = EqCondition(this, value)
    infix fun <T> Column<T>.notEq(value: T) = NotEqCondition(this, value)
    infix fun <T> Column<T>.greaterThan(value: T) = GreaterThanCondition(this, value)
    infix fun <T> Column<T>.greaterThanOrEq(value: T) = GreaterThanOrEqCondition(this, value)
    infix fun <T> Column<T>.lessThan(value: T) = LessThanCondition(this, value)
    infix fun <T> Column<T>.lessThanOrEq(value: T) = LessThanOrEqCondition(this, value)

    infix fun Column<String>.like(pattern: String) = LikeCondition(this, pattern)
    infix fun Column<String>.notLike(pattern: String) = NotLikeCondition(this, pattern)

    infix fun <T> Column<T>.inList(values: List<T>) = InCondition(this, values)
    infix fun <T> Column<T>.notInList(values: List<T>) = NotInCondition(this, values)

    fun <T> Column<T>.isNull() = IsNullCondition(this)
    fun <T> Column<T>.isNotNull() = IsNotNullCondition(this)

    fun <T> Column<T>.between(range: ClosedRange<T>): Condition where T : Comparable<T> {
        return BetweenCondition(this, range.start, range.endInclusive)
    }

    infix fun Condition.and(other: Condition) = AndCondition(this, other)
    infix fun Condition.or(other: Condition) = OrCondition(this, other)
    fun not(condition: Condition) = NotCondition(condition)

    fun raw(sql: String, vararg params: Any) = RawCondition(sql, params.toList())
}