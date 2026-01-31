import core.Database
import core.DatabaseConfiguration
import schema.Table

fun main() {

    val orm = Database(object : DatabaseConfiguration {
        override val url = "jdbc:postgresql://ep-lively-mode-ag9j92dh-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require"
        override val username: String = "neondb_owner"
        override val password: String = "npg_MIqDV6lkhGL9"
    })

    val usersTableModel = object : Table("users") {
        val id = integer("id").autoIncrement().primaryKey()
        val username = varchar("username", 50)
            .notNull()
            .unique()

        val email = varchar("email", 255)
            .notNull()
            .unique(constraintName = "uq_users_email_custom")
            .check("email LIKE '%@%'", constraintName = "chk_email_format")

        val age = integer("age")
            .nullable()
            .check("age >= 0 AND age <= 150", "chk_age_range")

        val status = varchar("status", 20)
            .notNull()
            .default("active")
            .check("status IN ('active', 'inactive', 'banned')", "chk_status_values")
    }

    println(usersTableModel)

    orm.closeConnection()
}