package execution

import schema.Column

/**
 * Repräsentiert eine Zeile aus dem ResultSet
 * ermöglicht typsicheren Zugriff auf Spalten-Werte
 */
class ResultRow(private val data: Map<String, Any?>) {

    operator fun <T> get(column: Column<T>): T {
        val value = data[column.name]
            ?: throw NoSuchElementException("Column '${column.name}' not found in result")

        @Suppress("UNCHECKED_CAST")
        return value as T
    }

    fun <T> getOrNull(column: Column<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return data[column.name] as? T
    }

    fun getString(columnName: String): String? = data[columnName] as? String
    fun getInt(columnName: String): Int? = data[columnName] as? Int
    fun getLong(columnName: String): Long? = data[columnName] as? Long
    fun getBoolean(columnName: String): Boolean? = data[columnName] as? Boolean

    fun getColumnNames(): Set<String> = data.keys

    fun hasColumn(column: Column<*>): Boolean = data.containsKey(column.name)
    fun hasColumn(columnName: String): Boolean = data.containsKey(columnName)

    override fun toString(): String {
        return "ResultRow(${data.entries.joinToString(", ") { "${it.key}=${it.value}" }})"
    }

    fun toMap(): Map<String, Any?> = data.toMap()
}