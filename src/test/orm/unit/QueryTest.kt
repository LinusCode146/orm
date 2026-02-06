package unit

import core.Database
import core.DatabaseConfiguration
import execution.QueryExecutor
import execution.ResultRow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import query.SelectQuery
import schema.Table
import java.math.BigDecimal
import java.math.RoundingMode

class QueryTest {
    private val dbCars = listOf<Map<String, Any>>(
        mapOf(
            "id" to 1,
            "make" to "BMW",
            "model" to "V3",
            "year" to 2010,
            "color" to "red",
            "price" to BigDecimal("100.00").setScale(2, RoundingMode.UNNECESSARY),
            "mileage" to 3,
            "is_electric" to true
        ),
        mapOf(
            "id" to 2,
            "make" to "VW",
            "model" to "V2",
            "year" to 2013,
            "color" to "blue",
            "price" to BigDecimal("100.00").setScale(2, RoundingMode.UNNECESSARY),
            "mileage" to 4,
            "is_electric" to false
        ),
        mapOf(
            "id" to 3,
            "make" to "Ford",
            "model" to "Galaxy",
            "year" to 2015,
            "color" to "pink",
            "price" to BigDecimal("100.00").setScale(2, RoundingMode.UNNECESSARY),
            "mileage" to 4,
            "is_electric" to false
        ),
    )

    private val carsModel = object : Table("cars") {
        val id = integer("id").autoIncrement().primaryKey()
        val make = varchar("make", 50)
            .notNull()

        val model = varchar("email", 50)
            .notNull()
            .unique(constraintName = "car_model")

        val year = integer("year")
            .nullable()
            .check("year >= 0 AND year <= 2027", "chk_year_range")

        val color = varchar("color", 30)
            .notNull()
            .default("active")
            .check("status IN ('active', 'inactive', 'banned')", "chk_status_values")
    }

    fun connect(): QueryExecutor {
        val orm = Database(object : DatabaseConfiguration {
            override val url = "jdbc:postgresql://ep-lively-mode-ag9j92dh-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require"
            override val username: String = "neondb_owner"
            override val password: String = "npg_MIqDV6lkhGL9"
        })
        val executor = QueryExecutor { orm.connection }

        return executor
    }

    @Test
    fun queryAllCarsTest() {
        for(i in 0..<dbCars.size) {

            val executor = connect()
            val selectQuery = SelectQuery(carsModel, executor)
                .where { carsModel.id eq i+1}

            val (selectSql, selectParams) = selectQuery.buildSql()

            val result = executor.executeQuerySingle(selectSql, selectParams)

            assertEquals(ResultRow(dbCars[i]), result)
        }
    }

    @Test
    fun queryCarsWithWhereTest() {
        val executor = connect()
        val selectQuery = SelectQuery(carsModel, executor)
            .where { carsModel.year greaterThan 2011}

        val (selectSql, selectParams) = selectQuery.buildSql()

        val result = executor.executeQuery(selectSql, selectParams)

        assertEquals(listOf(
            ResultRow(dbCars[1]), ResultRow(dbCars[2])
        ), result)

    }

    @Test
    fun queryChainingTest() {
        val executor = connect()
        val selectQuery = SelectQuery(carsModel, executor)
            .where { carsModel.year greaterThan 2011 }
            .where { carsModel.make notEq "Ford" }

        val (selectSql, selectParams) = selectQuery.buildSql()

        val result = executor.executeQuerySingle(selectSql, selectParams)

        assertEquals(ResultRow(dbCars[1]), result)
    }

}