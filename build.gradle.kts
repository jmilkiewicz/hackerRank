plugins {
    kotlin("jvm") version "1.9.23"
}

group = "pl.softmil"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
    testImplementation("org.hamcrest:hamcrest:2.2")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(18)
}