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
    testImplementation(kotlin("test"))

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
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}