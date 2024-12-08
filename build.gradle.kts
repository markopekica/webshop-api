plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // Ktor
    implementation("io.ktor:ktor-server-core:2.3.1")
    implementation("io.ktor:ktor-server-netty:2.3.1")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.1")
    testImplementation("io.ktor:ktor-server-tests:2.3.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.0")

    // remove SLF4J errors
    // SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
    implementation("ch.qos.logback:logback-classic:1.4.11")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}