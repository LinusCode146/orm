# KotlinORM - Drizzle-inspiriertes ORM für Kotlin

## Syntax-Ziele

Ein modernes, typsicheres ORM für Kotlin mit einer deklarativen, intuitiven API inspiriert von Drizzle ORM.

---

## 1. Schema-Definition

### Tabellen definieren

```kotlin
object Users : Table("users") {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 255).notNull()
    val email = varchar("email", 255).unique().notNull()
    val age = integer("age").nullable()
    val createdAt = timestamp("created_at").defaultNow()
}

object Posts : Table("posts") {
    val id = integer("id").autoIncrement().primaryKey()
    val title = varchar("title", 255).notNull()
    val content = text("content").notNull()
    val authorId = integer("author_id").references(Users.id).notNull()
    val publishedAt = timestamp("published_at").nullable()
}
```

### Beziehungen

```kotlin
object Users : Table("users") {
    // ... Spalten ...
    
    // Relationen
    val posts = hasMany(Posts, Posts.authorId)
}

object Posts : Table("posts") {
    // ... Spalten ...
    
    val author = belongsTo(Users, Posts.authorId)
}
```

---

## 2. Datenbank-Verbindung

```kotlin
val db = Database.connect(
    url = "jdbc:postgresql://localhost:5432/mydb",
    driver = "org.postgresql.Driver",
    user = "user",
    password = "password"
)

// Oder mit DSL
val db = database {
    postgresql {
        host = "localhost"
        port = 5432
        database = "mydb"
        user = "user"
        password = "password"
    }
}
```

---

## 3. Query-Syntax

### SELECT Queries

```kotlin
// Einfacher Select
val users = db.select(Users).execute()

// Mit Bedingungen
val adults = db.select(Users)
    .where { Users.age greaterThan 18 }
    .execute()

// Mehrere Bedingungen
val activeUsers = db.select(Users)
    .where { 
        (Users.age greaterThan 18) and (Users.email like "%@gmail.com")
    }
    .execute()

// Mit Limit und Offset
val page = db.select(Users)
    .limit(10)
    .offset(20)
    .execute()

// Spezifische Spalten
val names = db.select(Users.name, Users.email)
    .where { Users.age greaterThan 18 }
    .execute()

// Mit Sortierung
val sorted = db.select(Users)
    .orderBy(Users.createdAt.desc(), Users.name.asc())
    .execute()
```

### Aggregation

```kotlin
// Count
val userCount = db.select(Users.id.count())
    .where { Users.age greaterThan 18 }
    .executeSingle()

// Gruppierung
val usersByAge = db.select(Users.age, Users.id.count().alias("count"))
    .groupBy(Users.age)
    .execute()
```

### JOINS

```kotlin
// Inner Join
val postsWithAuthors = db.select(Posts, Users)
    .innerJoin(Users) { Posts.authorId eq Users.id }
    .execute()

// Left Join
val allPosts = db.select(Posts, Users)
    .leftJoin(Users) { Posts.authorId eq Users.id }
    .execute()

// Mit Relationen (vereinfacht)
val postsWithAuthors = db.select(Posts)
    .with(Posts.author)
    .execute()
```

---

## 4. INSERT Queries

```kotlin
// Einzelner Insert
val newUser = db.insert(Users)
    .values {
        name = "Max Mustermann"
        email = "max@example.com"
        age = 25
    }
    .returning(Users.id)
    .executeSingle()

// Mehrere Inserts
db.insert(Users)
    .values(
        { name = "Alice"; email = "alice@example.com" },
        { name = "Bob"; email = "bob@example.com" }
    )
    .execute()

// Mit Objekt
data class NewUser(val name: String, val email: String, val age: Int?)

val user = NewUser("Charlie", "charlie@example.com", 30)
db.insert(Users)
    .values(user)
    .execute()
```

---

## 5. UPDATE Queries

```kotlin
// Einfaches Update
db.update(Users)
    .set {
        age = 26
    }
    .where { Users.id eq 1 }
    .execute()

// Mehrere Felder
db.update(Users)
    .set {
        name = "Max Neumann"
        email = "max.neu@example.com"
    }
    .where { Users.id eq 1 }
    .execute()

// Mit Expression
db.update(Users)
    .set {
        age = Users.age + 1
    }
    .where { Users.age lessThan 100 }
    .execute()
```

---

## 6. DELETE Queries

```kotlin
// Einfaches Delete
db.delete(Users)
    .where { Users.id eq 1 }
    .execute()

// Mehrere Zeilen
db.delete(Posts)
    .where { Posts.publishedAt.isNull() }
    .execute()

// Alle Zeilen (mit Sicherheit)
db.delete(Users)
    .all()
    .execute()
```

---

## 7. Transaktionen

```kotlin
db.transaction {
    val userId = insert(Users)
        .values { name = "Test"; email = "test@example.com" }
        .returning(Users.id)
        .executeSingle()
    
    insert(Posts)
        .values {
            title = "Erster Post"
            content = "Inhalt..."
            authorId = userId
        }
        .execute()
    
    // Bei Fehler: automatisches Rollback
    // Bei Erfolg: automatisches Commit
}
```

---

## 8. Migrations

```kotlin
// Migration-Datei
class CreateUserTable : Migration("001_create_users") {
    override fun up() {
        createTable(Users) {
            // Schema wird automatisch aus Table-Definition generiert
        }
    }
    
    override fun down() {
        dropTable(Users)
    }
}

// Migration ausführen
db.migrate()

// Oder programmatisch
db.migrations.run(CreateUserTable())
```

---

## 9. Query Builder Features

### Subqueries

```kotlin
val activeUserIds = db.select(Users.id)
    .where { Users.age greaterThan 18 }
    .asSubquery()

val posts = db.select(Posts)
    .where { Posts.authorId inSubquery activeUserIds }
    .execute()
```

### Raw SQL Fallback

```kotlin
// Für komplexe Queries
val result = db.raw(
    "SELECT * FROM users WHERE age > ? AND email LIKE ?",
    18, "%@gmail.com"
).execute()

// Raw in Query Builder
val users = db.select(Users)
    .where { raw("LOWER(name) = ?", "max") }
    .execute()
```

---

## 10. Typsicherheit & Nullability

```kotlin
// Nullable vs. NotNull Spalten
val users = db.select(Users).execute()

users.forEach { user ->
    val name: String = user.name          // notNull() -> String
    val age: Int? = user.age              // nullable() -> Int?
    val id: Int = user.id                 // primaryKey -> String
}

// Compile-Zeit Fehler bei falschen Typen
db.update(Users)
    .set {
        age = "invalid"  // ❌ Compile-Fehler: Type mismatch
    }
```

---

## 11. DSL Operatoren

```kotlin
// Vergleichsoperatoren
where { Users.age eq 25 }
where { Users.age notEq 25 }
where { Users.age greaterThan 18 }
where { Users.age greaterThanOrEq 18 }
where { Users.age lessThan 65 }
where { Users.age lessThanOrEq 65 }

// String-Operatoren
where { Users.email like "%@gmail.com" }
where { Users.email notLike "%spam%" }
where { Users.name.isNull() }
where { Users.age.isNotNull() }

// Listen
where { Users.id inList listOf(1, 2, 3) }
where { Users.id notInList listOf(4, 5) }

// Logische Operatoren
where { (Users.age greaterThan 18) and (Users.email like "%@gmail.com") }
where { (Users.age lessThan 18) or (Users.age greaterThan 65) }
where { not(Users.email like "%spam%") }

// Between
where { Users.age between 18..65 }
```

---

## 12. Ergebnis-Mapping

```kotlin
// Als Data Class
data class UserDTO(val id: Int, val name: String, val email: String)

val users = db.select(Users.id, Users.name, Users.email)
    .execute()
    .map { UserDTO(it[Users.id], it[Users.name], it[Users.email]) }

// Oder mit automatischem Mapping
val users = db.select(Users)
    .execute()
    .mapTo<UserDTO>()
```

---

## Design-Prinzipien

1. **Typsicherheit**: Compile-Zeit Überprüfung aller Queries
2. **Intuitive DSL**: Lesbar wie natürliche Sprache
3. **Null-Safety**: Kotlin's Type-System voll ausnutzen
4. **Flexibilität**: Raw SQL für Edge-Cases
5. **Performance**: Lazy Evaluation wo möglich
6. **Migrations**: Integriertes Schema-Management
7. **Multi-DB Support**: PostgreSQL, MySQL, SQLite, etc.

---

## Technische Features

- ✅ Automatisches Schema-Inference
- ✅ Prepared Statements (SQL Injection Schutz)
- ✅ Connection Pooling
- ✅ Lazy Loading
- ✅ Eager Loading (N+1 Problem Vermeidung)
- ✅ Batch Operations
- ✅ Custom Type Converters
- ✅ Hooks (beforeInsert, afterUpdate, etc.)