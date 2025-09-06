plugins {
    kotlin("jvm") version "2.2.0"
    application
}

group = "ansi5"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("ansi5.blocks.LineKt")
}