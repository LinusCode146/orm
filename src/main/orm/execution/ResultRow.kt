package execution

import schema.Column
import schema.columns.IntegerColumn
import schema.columns.VarcharColumn
import schema.columns.TextColumn
import java.sql.ResultSet

/**
 * Repräsentiert eine Zeile aus dem ResultSet
 * Ermöglicht typsicheren Zugriff auf Spalten-Werte
 */
class ResultRow(private val data: Map<String, Any?>) {

    // Type-safe Column Access
    operator fun <T> get(column: Column<T>): T {
        val value = data[column.name]
            ?: throw NoSuchElementException("Column '${column.name}' not found in result")

        @Suppress("UNCHECKED_CAST")
        return value as T
    }

    // Nullable Column Access
    fun <T> getOrNull(column: Column<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return data[column.name] as? T
    }

    // By Name Access (weniger typsicher)
    fun getString(columnName: String): String? = data[columnName] as? String
    fun getInt(columnName: String): Int? = data[columnName] as? Int
    fun getLong(columnName: String): Long? = data[columnName] as? Long
    fun getBoolean(columnName: String): Boolean? = data[columnName] as? Boolean

    // Alle Spalten
    fun getColumnNames(): Set<String> = data.keys

    // Prüfen ob Column existiert
    fun hasColumn(column: Column<*>): Boolean = data.containsKey(column.name)
    fun hasColumn(columnName: String): Boolean = data.containsKey(columnName)

    // Debugging
    override fun toString(): String {
        return "ResultRow(${data.entries.joinToString(", ") { "${it.key}=${it.value}" }})"
    }

    // Zu Map konvertieren
    fun toMap(): Map<String, Any?> = data.toMap()
}