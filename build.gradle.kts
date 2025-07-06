import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar


plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    //id("io.ktor.plugin") version "2.3.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    // Remove the classifier (optional)
    archiveClassifier.set("")
    archiveFileName.set("webshop-api-all.jar")
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
    project.setProperty("mainClassName", "webshop.MainKt")
    // Configure the manifest
    manifest {
        attributes["Main-Class"] = "webshop.MainKt"
    }
}


group = "org.example"
version = "1.0-SNAPSHOT"

val ktor_version = "2.3.1"

repositories {
    mavenCentral()
}

dependencies {
    //testImplementation(kotlin("test"))

    // Ktor
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.0")

    // remove SLF4J errors
    // SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
    implementation("ch.qos.logback:logback-classic:1.4.11")

    // Ktor - Status Pages
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")

    // Unit Tests
    testImplementation("io.ktor:ktor-server-tests-jvm:2.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Databse
    implementation("com.datastax.oss:java-driver-core:4.15.0")
    implementation("com.datastax.oss:java-driver-query-builder:4.15.0")

    // Mockk
    testImplementation("io.mockk:mockk:1.13.5")
    //testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.0")
    testImplementation("io.ktor:ktor-server-tests:2.3.1")

    // test containers
    testImplementation("org.testcontainers:testcontainers:1.19.0")
    testImplementation("org.testcontainers:cassandra:1.19.0")

    /*
    implementation("com.datastax.oss:java-driver-core:4.x.x") {
        // Exclude Tinkerpop-related modules
        exclude(group = "org.apache.tinkerpop", module = "gremlin-tinkergraph-structure")
        exclude(group = "org.apache.tinkerpop", module = "gremlin-core")
    }

     */

    // Logging
    implementation("io.ktor:ktor-server-call-logging:${ktor_version}")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
sourceSets {
    test {
        java.srcDirs("src/test/kotlin")
    }
}
kotlin {
    jvmToolchain(17)
}

tasks.withType<Jar> {
    manifest {
        // Specify the fully qualified name of the main class.
        // For Kotlin, if your main function is in Main.kt and in package "webshop",
        // the main class is typically "webshop.MainKt"
        attributes["Main-Class"] = "webshop.MainKt"
    }
}

