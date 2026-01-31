package schema.columns

import schema.Column

data class TextColumn(
    override val name: String,
    override var nullable: Boolean = true,
    override var defaultValue: String? = null,
    val textType: TextType = TextType.TEXT,
    val collation: String? = null
) : Column<String>() {

    override fun notNull(): TextColumn  {
        nullable = false
        return this
    }
    override fun nullable(): TextColumn {
        nullable = true
        return this
    }

    override fun default(value: String): TextColumn {
        defaultValue = value
        return this
    }

    fun tiny() = copy(textType = TextType.TINYTEXT)
    fun medium() = copy(textType = TextType.MEDIUMTEXT)
    fun long() = copy(textType = TextType.LONGTEXT)
    fun collation(collation: String) = copy(collation = collation)

    enum class TextType(val maxLength: Long) {
        TINYTEXT(255),           // 2^8 - 1
        TEXT(65_535),            // 2^16 - 1
        MEDIUMTEXT(16_777_215),  // 2^24 - 1
        LONGTEXT(4_294_967_295)  // 2^32 - 1
    }
}