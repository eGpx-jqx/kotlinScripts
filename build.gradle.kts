plugins {
    kotlin("jvm") version "2.0.10"
}

group = "com.egpx.learn"
version = "1.0-SNAPSHOT"

val kotlinVersion: String by extra("2.0.20")
val kotlinCoroutinesVersion: String by extra("1.9.0-RC.2")

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}