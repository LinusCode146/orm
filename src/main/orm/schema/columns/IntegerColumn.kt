package schema.columns

import schema.Column

data class IntegerColumn(
    override val name: String,
    override var nullable: Boolean = true,
    override var defaultValue: Int? = null,
    var autoIncrement: Boolean = false
) : Column<Int>() {

    override fun notNull(): IntegerColumn {
        nullable = false
        return this
    }
    override fun nullable(): IntegerColumn {
        nullable = true
        return this
    }

    override fun default(value: Int): IntegerColumn {
        defaultValue = value
        return this
    }

    fun autoIncrement(): IntegerColumn {
        autoIncrement = true
        return this
    }
}