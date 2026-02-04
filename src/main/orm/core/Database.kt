package core

import java.sql.Connection
import java.sql.DriverManager

class Database(config: DatabaseConfiguration) {
    private val _connection: Connection = DriverManager.getConnection(config.url, config.username, config.password)

    fun getConnection(): Connection = _connection

    fun closeConnection() {
        _connection.close()
    }
}