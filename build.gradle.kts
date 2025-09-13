import org.gradle.kotlin.dsl.withType

plugins {
    id("org.springframework.boot") version "3.5.5" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "com.toast1ng"
    version = "1.0.0-SNAPSHOT"

    tasks.withType<JavaCompile>{
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    repositories {
        mavenCentral()
    }
}