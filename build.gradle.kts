
plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    //id("io.ktor.plugin") version "2.3.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

application {
    mainClass.set("webshop.Main")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    // Remove the classifier (optional)
    archiveClassifier.set("")
    archiveFileName.set("webshop-api-all.jar")
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
    // Configure the manifest
    manifest {
        attributes["Main-Class"] = "webshop.MainKt"
    }
}


group = "org.example"
version = "1.0-SNAPSHOT"

val ktor_version = "3.2.3"

repositories {
    mavenCentral()
}

dependencies {

    // Ktor
    implementation(platform("io.netty:netty-bom:4.1.118.Final"))
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.0")

    // remove SLF4J errors
    // SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
    implementation("ch.qos.logback:logback-classic:1.5.13")

    // Ktor - Status Pages
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")

    // Unit Tests
    testImplementation("io.ktor:ktor-server-test-host-jvm:3.2.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Databse - Cassandra driver
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2") // neutralize vulnerability from below line: implementation("com.datastax.oss:java-driver-core:4.17.0")
    //implementation("com.datastax.oss:java-driver-core:4.17.0")
    implementation(platform("org.apache.cassandra:java-driver-bom:4.19.0"))
    implementation("org.apache.cassandra:java-driver-core")
    implementation("com.datastax.oss:java-driver-query-builder:4.15.0")

    // Mockk
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("io.ktor:ktor-server-tests-jvm")

    // test containers
    testImplementation("org.testcontainers:testcontainers:1.21.2")
    testImplementation("org.testcontainers:cassandra:1.21.2")

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

configurations.all {
    resolutionStrategy {
        // Apache Commons Compress: CVE-2023-42503 fix ≥1.24.0; CVE-2024-26308 fix ≥1.26.0
        force("org.apache.commons:commons-compress:1.26.0")
        // Guava: mitigacija za CVE-2020-8908
        force("com.google.guava:guava:32.1.3-jre")
    }
}