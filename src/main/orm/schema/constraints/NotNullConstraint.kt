package schema.constraints

import schema.Constraint

data class NotNullConstraint(
    val column: String
) : Constraint {
    override val name: String? = null
}