import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
    `maven-publish`
}

group = "pl.allegro.devskiller"
version = "1.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

repositories {
    mavenCentral()
}

val boltVersion = "1.20.0"
val slf4jVersion = "1.7.36"
val wiremockVersion = "2.32.0"
val mockkVersion = "1.12.2"
val jacksonVersion = "2.13.1"
val kotlinCliVersion = "0.3.4"
val junitVersion = "5.8.1"

dependencies {
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli:$kotlinCliVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.slack.api:bolt:$boltVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    testImplementation(kotlin("test-junit5"))
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("com.github.tomakehurst:wiremock-jre8:$wiremockVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
}

application {
    mainClass.set("pl.allegro.devskiller.AppKt")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = rootProject.name
            from(components["java"])
        }
    }
}
