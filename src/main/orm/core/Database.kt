package core

import java.sql.Connection
import java.sql.DriverManager

class Database(private val config: DatabaseConfiguration) {
    private val connection: Connection = DriverManager.getConnection(config.url, config.username, config.password)


    /*fun <T : Table> select(table: T): SelectQuery<T>
    fun <T : Table> insert(table: T): InsertQuery<T>
    fun <T : Table> update(table: T): UpdateQuery<T>
    fun <T : Table> delete(table: T): DeleteQuery<T>*/

    fun getConnection(): Connection = connection

    fun closeConnection() {
        connection.close()
    }
}