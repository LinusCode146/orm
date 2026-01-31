package schema.constraints
import schema.Constraint

data class ForeignKeyConstraint(
    val column: String,
    val referencedTable: String,
    val referencedColumn: String,
    val onDelete: ReferentialAction = ReferentialAction.NO_ACTION,
    val onUpdate: ReferentialAction = ReferentialAction.NO_ACTION,
    override val name: String? = null
) : Constraint {

    fun getConstraintName(tableName: String): String {
        return name ?: "fk_${tableName}_${column}_${referencedTable}_${referencedColumn}"
    }

    enum class ReferentialAction {
        CASCADE,      // Löscht/Updated abhängige Zeilen
        SET_NULL,     // Setzt Fremdschlüssel auf NULL
        SET_DEFAULT,  // Setzt Fremdschlüssel auf Default-Wert
        RESTRICT,     // Verhindert Löschen/Update wenn abhängige Zeilen existieren
        NO_ACTION     // Wie RESTRICT, aber deferred check
    }
}
