package general

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.sql.DriverManager

class DatabaseConnectionTest {

    @Test
    fun testDatabaseConnection() {
        val url = "jdbc:postgresql://ep-lively-mode-ag9j92dh-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require"
        val user = "neondb_owner"
        val password = "npg_MIqDV6lkhGL9"

        val connection = DriverManager.getConnection(url, user, password)

        assertNotNull(connection, "Connection should not be null")
        assertTrue(connection.isValid(5), "Connection should be valid")
        assertFalse(connection.isClosed, "Connection should not be closed")

        connection.close()
        assertTrue(connection.isClosed, "Connection should be closed after close()")
    }

    @Test
    fun testQueryExecution() {
        val url = "jdbc:postgresql://ep-lively-mode-ag9j92dh-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require"
        val user = "neondb_owner"
        val password = "npg_MIqDV6lkhGL9"

        val connection = DriverManager.getConnection(url, user, password)
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT 1 as test_value")

        assertTrue(resultSet.next(), "Result set should have at least one row")
        assertEquals(1, resultSet.getInt("test_value"), "Query should return 1")

        resultSet.close()
        statement.close()
        connection.close()
    }
}