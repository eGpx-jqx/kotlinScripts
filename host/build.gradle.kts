plugins {
    kotlin("jvm")
}

group = "com.egpx.learn"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val kotlinVersion: String by rootProject.extra
dependencies {
    implementation(project(":script"))
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host:$kotlinVersion")
    testImplementation("junit:junit:4.12")
    testRuntimeOnly("org.slf4j:slf4j-nop:1.7.28")
}

kotlin {
    jvmToolchain(21)
}