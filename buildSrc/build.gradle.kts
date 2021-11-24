plugins {
    `kotlin-dsl`
    id("org.gradle.test-retry") version "1.2.0"
}

repositories {
    mavenCentral()
}

tasks.test {
    retry {
        maxRetries.set(2)
        maxFailures.set(10)
        failOnPassedAfterRetry.set(false)
    }
}