package schema.columns

import schema.Column

data class BooleanColumn(
    override val name: String,
    override var nullable: Boolean = false,
    override var defaultValue: Boolean? = false,
): Column<Boolean>() {
    override fun notNull(): Column<Boolean> {
        nullable = false
        return this
    }

    override fun default(value: Boolean): Column<Boolean> {
        defaultValue = value
        return this
    }

    override fun nullable(): Column<Boolean> {
        nullable = true
        return this
    }
}