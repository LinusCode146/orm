package schema.constraints

import schema.Constraint

data class CheckConstraint(
    val condition: String,
    override val name: String? = null
) : Constraint {

    fun getConstraintName(tableName: String, columnName: String? = null): String {
        return name ?: buildString {
            append("chk_${tableName}")
            if (columnName != null) append("_${columnName}")
        }
    }
}
