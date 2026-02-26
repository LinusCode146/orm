plugins {
    kotlin("jvm") version "2.3.0"
    `java-library`
    id("com.gradleup.shadow") version "9.3.1"
}

group = "org.kotlin-orm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.10")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

java {
    withSourcesJar()
    withJavadocJar()
}

sourceSets {
    main {
        kotlin.srcDir("src/main/orm")
    }

    test {
        kotlin.srcDir("src/test/orm")
    }
}

tasks.test {
    useJUnitPlatform()
}

// Shadow plugin configuration to create a .jar file with dependencies
tasks.shadowJar {
    archiveClassifier.set("bundled") // add -all postfix
}
