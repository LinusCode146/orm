package schema.constraints

import schema.Constraint

data class PrimaryKeyConstraint(
    val columns: List<String>,
    override val name: String? = null,
    val autoName: Boolean = true
) : Constraint {

    constructor(column: String, name: String? = null) : this(listOf(column), name)

    fun getConstraintName(tableName: String): String {
        return name ?: if (autoName) {
            "pk_${tableName}_${columns.joinToString("_")}"
        } else {
            "PRIMARY"
        }
    }

    companion object {
        fun of(vararg columns: String, name: String? = null): PrimaryKeyConstraint {
            return PrimaryKeyConstraint(columns.toList(), name)
        }
    }
}