plugins {
    kotlin("jvm")
}

group = "com.egpx.learn"
version = "1.0-SNAPSHOT"


val kotlinVersion: String by rootProject.extra
val kotlinCoroutinesVersion: String by rootProject.extra
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
}

kotlin {
    jvmToolchain(21)
}