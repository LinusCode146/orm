import core.Database;
import core.DatabaseConfiguration;
import execution.QueryExecutor;
import query.SelectQuery;
import schema.Column;
import schema.Table;

// Ja, so kann Java mittlerweile auch aussehen, ganz ohne Main Klasse und `String[] args`
void main() {
    var orm = new Database(new DatabaseConfiguration() {
        @Override
        public String getPassword() {
            return "npg_MIqDV6lkhGL9";
        }

        @Override
        public String getUsername() {
            return "neondb_owner";
        }

        @Override
        public String getUrl() {
            return "jdbc:postgresql://ep-lively-mode-ag9j92dh-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require";
        }
    });

    var executor = new QueryExecutor(orm::getConnection);

    var carsModel = new Table("cars") {
        final Column<Integer> id;
        final Column<String> make;
        final Column<String> model;
        final Column<Integer> age;
        final Column<String> color;

        {
            this.id = integer("id").autoIncrement().primaryKey(null);
            this.make = varchar("make", 50).notNull();
            this.model = varchar("model", 50).notNull().unique("car_model");
            this.age = integer("age").nullable().check("age >= 0 AND age <= 2027", "chk_year_range");
            this.color = varchar("color", 30)
                    .notNull()
                    .useDefault("active")
                    .check("status IN ('active', 'inactive', 'banned')", "chk_status_values");
        }
    };

    IO.println();
    IO.println("Test class using Java");
    IO.println(carsModel);

    var selectQuery = new SelectQuery(carsModel, executor);
    var selectResult = selectQuery.buildSql();
    var selectSql = (String) selectResult.getFirst();
    var selectParams = (List<Object>) selectResult.getSecond();

    IO.println("SQL: " + selectSql);
    IO.println("Params: " + selectParams);
    var res = executor.executeQuerySingle(selectSql, selectParams);

    if (res != null) {
        IO.println(res);
        IO.println(res.get(carsModel.model));
    }

    orm.closeConnection();
}
