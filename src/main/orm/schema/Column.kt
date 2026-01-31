package schema

import schema.constraints.CheckConstraint
import schema.constraints.ForeignKeyConstraint
import schema.constraints.PrimaryKeyConstraint
import schema.constraints.UniqueConstraint

abstract class Column<T> {
    abstract val name: String
    abstract var nullable: Boolean
    abstract var defaultValue: T?

    internal val constraints = mutableListOf<Constraint>()

    abstract fun notNull(): Column<T>
    abstract fun nullable(): Column<T>
    abstract fun default(value: T): Column<T>

    fun primaryKey(constraintName: String? = null): Column<T> {
        constraints.add(PrimaryKeyConstraint(name, constraintName))
        nullable = false
        return this
    }

    fun unique(constraintName: String? = null): Column<T> {
        constraints.add(UniqueConstraint(name, constraintName))
        return this
    }

    fun check(condition: String, constraintName: String? = null): Column<T> {
        constraints.add(CheckConstraint(condition, constraintName))
        return this
    }


    fun references(
        table: Table,
        column: Column<T>,
        onDelete: ForeignKeyConstraint.ReferentialAction = ForeignKeyConstraint.ReferentialAction.NO_ACTION,
        onUpdate: ForeignKeyConstraint.ReferentialAction = ForeignKeyConstraint.ReferentialAction.NO_ACTION,
        constraintName: String? = null
    ): Column<T> {
        constraints.add(
            ForeignKeyConstraint(
                column = name,
                referencedTable = table.tableName,
                referencedColumn = column.name,
                onDelete = onDelete,
                onUpdate = onUpdate,
                name = constraintName
            )
        )
        return this
    }
}

