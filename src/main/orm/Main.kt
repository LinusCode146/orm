import java.sql.DriverManager

fun main() {
    val url = "jdbc:postgresql://ep-lively-mode-ag9j92dh-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require"
    val user = "neondb_owner"
    val password = "npg_MIqDV6lkhGL9"

    val connection = DriverManager.getConnection(url, user, password)

    // Get all columns from cars table
    val statement = connection.createStatement()
    val resultSet = statement.executeQuery("""
        SELECT column_name, data_type, is_nullable
        FROM information_schema.columns
        WHERE table_name = 'cars'
        ORDER BY ordinal_position
    """)

    println("Columns in 'cars' table:")
    while (resultSet.next()) {
        val columnName = resultSet.getString("column_name")
        val dataType = resultSet.getString("data_type")
        val isNullable = resultSet.getString("is_nullable")
        println("- $columnName ($dataType, nullable: $isNullable)")
    }

    resultSet.close()
    statement.close()
    connection.close()
}