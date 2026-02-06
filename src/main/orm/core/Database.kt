package core

import java.sql.Connection
import java.sql.DriverManager

class Database(config: DatabaseConfiguration) {
    internal val _connection: Connection = DriverManager.getConnection(config.url, config.username, config.password)
    val connection: Connection get() = _connection

    fun closeConnection() {
        _connection.close()
    }
}