plugins {
    kotlin("jvm") version "1.9.21"
    `maven-publish`
}

group = "io.github.mmarco94"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    // DBUS APIs
    implementation("com.github.hypfvieh:dbus-java-core:4.3.1")
    // implementation("com.github.hypfvieh:dbus-java-utils:4.3.1")
    implementation("com.github.hypfvieh:dbus-java-transport-native-unixsocket:4.3.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
