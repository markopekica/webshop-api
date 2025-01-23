plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    application
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
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")// <--- This is crucial
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Databse
    implementation("com.datastax.oss:java-driver-core:4.15.0")
    implementation("com.datastax.oss:java-driver-query-builder:4.15.0")
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