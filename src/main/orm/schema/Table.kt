package schema

import schema.columns.IntegerColumn
import schema.columns.TextColumn
import schema.columns.VarcharColumn
import schema.constraints.CheckConstraint
import schema.constraints.ForeignKeyConstraint
import schema.constraints.PrimaryKeyConstraint
import schema.constraints.UniqueConstraint

abstract class Table(val tableName: String) {
    internal val _columns = mutableListOf<Column<*>>()
    val columns: List<Column<*>> get() = _columns
    internal val tableConstraints = mutableListOf<Constraint>()

    protected fun integer(name: String): IntegerColumn {
        val column = IntegerColumn(name)
        _columns.add(column)
        return column
    }

    protected fun varchar(name: String, length: Int): VarcharColumn {
        val column = VarcharColumn(name, length)
        _columns.add(column)
        return column
    }

    protected fun text(name: String): TextColumn {
        val column = TextColumn(name)
        _columns.add(column)
        return column
    }

    protected fun primaryKey(vararg columns: Column<*>, name: String? = null) {
        tableConstraints.add(
            PrimaryKeyConstraint(columns.map { it.name }, name)
        )
    }

    protected fun unique(vararg columns: Column<*>, name: String? = null) {
        tableConstraints.add(
            UniqueConstraint(columns.map { it.name }, name)
        )
    }

    protected fun check(condition: String, name: String? = null) {
        tableConstraints.add(CheckConstraint(condition, name))
    }

    fun getAllConstraints(): List<Constraint> {
        val columnConstraints = columns.flatMap { it.constraints }
        return columnConstraints + tableConstraints
    }

    override fun toString(): String {
        return buildString {
            appendLine("Table: $tableName")
            appendLine("=" .repeat(60))
            appendLine()

            // Spalten
            appendLine("Columns:")
            columns.forEach { column ->
                appendLine("  - ${column.toDetailString()}")
            }

            // Table-Level Constraints
            if (tableConstraints.isNotEmpty()) {
                appendLine()
                appendLine("Table Constraints:")
                tableConstraints.forEach { constraint ->
                    appendLine("  - ${constraint.toDetailString(tableName)}")
                }
            }
        }
    }
}

fun Column<*>.toDetailString(): String {
    return buildString {
        append(name)
        append(": ")

        // Type
        when (this@toDetailString) {
            is IntegerColumn -> {
                append("INTEGER")
                if (autoIncrement) append(" AUTO_INCREMENT")
            }
            is VarcharColumn -> append("VARCHAR(${this@toDetailString.length})")
            is TextColumn -> append(textType.name)
        }

        // Nullable
        if (!this@toDetailString.nullable) append(" NOT NULL")

        // Default
        if (this@toDetailString.defaultValue != null) {
            append(" DEFAULT ")
            when (this@toDetailString.defaultValue) {
                is String -> append("'$defaultValue'")
                else -> append(defaultValue)
            }
        }

        // Constraints
        val constraintStrings = constraints.map { constraint ->
            when (constraint) {
                is PrimaryKeyConstraint -> "PRIMARY KEY"
                is UniqueConstraint -> "UNIQUE"
                is CheckConstraint -> "CHECK (${constraint.condition})"
                is ForeignKeyConstraint ->
                    "REFERENCES ${constraint.referencedTable}(${constraint.referencedColumn})"
                else -> constraint.toString()
            }
        }

        if (constraintStrings.isNotEmpty()) {
            append(" [${constraintStrings.joinToString(", ")}]")
        }
    }
}

fun Constraint.toDetailString(tableName: String): String {
    return when (this) {
        is PrimaryKeyConstraint ->
            "PRIMARY KEY (${columns.joinToString(", ")}) [${getConstraintName(tableName)}]"
        is UniqueConstraint ->
            "UNIQUE (${columns.joinToString(", ")}) [${getConstraintName(tableName)}]"
        is CheckConstraint ->
            "CHECK ($condition) [${getConstraintName(tableName)}]"
        is ForeignKeyConstraint ->
            "FOREIGN KEY ($column) REFERENCES $referencedTable($referencedColumn) " +
                    "ON DELETE $onDelete ON UPDATE $onUpdate [${getConstraintName(tableName)}]"
        else -> toString()
    }
}