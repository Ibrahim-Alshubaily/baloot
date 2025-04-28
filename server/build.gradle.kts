plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.alshubaily.chess"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.github.luben:zstd-jni:1.5.5-11")

    implementation("org.postgresql:postgresql:42.7.1")
    implementation("org.apache.kafka:kafka-clients:3.6.0")

    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

springBoot {
    mainClass.set("com.alshubaily.chess.server.ServerApplicationKt")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<JavaExec>("runLichessLoader") {
    group = "lichess"
    description = "Run the Lichess Eval Importer from remote"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("com.alshubaily.chess.server.data.loader.LichessEvalImporter")
}

tasks.register<JavaExec>("runChessEvaluationsPublisher") {
    group = "lichess"
    description = "Run the Kafka Publisher that streams chess evaluation samples"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.alshubaily.chess.server.data.publisher.ChessEvaluationsPublisher")
}
