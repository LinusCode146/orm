package schema.constraints

import schema.Constraint

data class UniqueConstraint(
    val columns: List<String>,
    override val name: String? = null
) : Constraint {

    constructor(column: String, name: String? = null) : this(listOf(column), name)

    fun getConstraintName(tableName: String): String {
        return name ?: "uq_${tableName}_${columns.joinToString("_")}"
    }

    companion object {
        fun of(vararg columns: String, name: String? = null): UniqueConstraint {
            return UniqueConstraint(columns.toList(), name)
        }
    }
}
