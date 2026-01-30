import java.sql.DriverManager

fun main() {
    val url = "jdbc:postgresql://ep-lively-mode-ag9j92dh-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require"
    val user = "neondb_owner"
    val password = "npg_MIqDV6lkhGL9"

    val connection = DriverManager.getConnection(url, user, password)

    val createTableSQL = """
        CREATE TABLE IF NOT EXISTS cars (
            id SERIAL PRIMARY KEY,
            make VARCHAR(50) NOT NULL,
            model VARCHAR(50) NOT NULL,
            year INT NOT NULL,
            color VARCHAR(30),
            price DECIMAL(10, 2),
            mileage INT,
            is_electric BOOLEAN DEFAULT FALSE
        )
    """.trimIndent()

    val statement = connection.createStatement()
    statement.execute(createTableSQL)

    println("Table 'cars' created successfully!")

    statement.close()
    connection.close()
}