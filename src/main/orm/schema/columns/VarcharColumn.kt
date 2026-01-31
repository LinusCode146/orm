package schema.columns

import schema.Column

data class VarcharColumn(
    override val name: String,
    val length: Int,
    override var nullable: Boolean = true,
    override var defaultValue: String? = null
) : Column<String>() {

    init {
        require(length > 0) { "VARCHAR length must be > 0" }
        require(length <= 65535) { "VARCHAR length must be <= 65535" }
    }

    override fun notNull(): VarcharColumn  {
        nullable = false
        return this
    }
    override fun nullable(): VarcharColumn {
        nullable = true
        return this
    }

    override fun default(value: String): VarcharColumn {
        require(value.length <= length) {
            "Default value '$value' (${value.length} chars) exceeds VARCHAR($length)"
        }
        defaultValue = value
        return this
    }
}