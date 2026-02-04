import core.Database
import core.DatabaseConfiguration
import execution.QueryExecutor
import query.SelectQuery
import schema.Table

fun main() {

    val orm = Database(object : DatabaseConfiguration {
        override val url = "jdbc:postgresql://ep-lively-mode-ag9j92dh-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require"
        override val username: String = "neondb_owner"
        override val password: String = "npg_MIqDV6lkhGL9"
    })

    val executor = QueryExecutor { orm.getConnection() }

    val carsModel = object : Table("cars") {
        val id = integer("id").autoIncrement().primaryKey()
        val make = varchar("make", 50)
            .notNull()

        val model = varchar("email", 50)
            .notNull()
            .unique(constraintName = "car_model")

        val year = integer("age")
            .nullable()
            .check("age >= 0 AND age <= 2027", "chk_year_range")

        val color = varchar("color", 30)
            .notNull()
            .default("active")
            .check("status IN ('active', 'inactive', 'banned')", "chk_status_values")
    }
    println()
    println(carsModel)

    val selectQuery = SelectQuery(carsModel, executor)
        .where { carsModel.id eq 1 }
        .limit(1)

    val (selectSql, selectParams) = selectQuery.buildSql()


    println("SQL: $selectSql")
    println("Params: $selectParams")
    println(executor.executeQuerySingle(selectSql, selectParams))


    orm.closeConnection()
}