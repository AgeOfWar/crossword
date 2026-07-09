plugins {
    id("java")
    id("application")
}

group = "io.github.ageofwar"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("io.github.ageofwar.Main")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ow2.sat4j:org.ow2.sat4j.core:2.3.6")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    test {
        useJUnitPlatform()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
