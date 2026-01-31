package schema.constraints
import schema.Constraint

data class DefaultConstraint(
    val column: String,
    val value: Any
) : Constraint {
    override val name: String? = null
}