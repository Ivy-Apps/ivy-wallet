plugins {
    `kotlin-dsl`
    id("org.gradle.test-retry") version "1.2.0"
}

tasks.test {
    retry {
        maxRetries.set(2)
        maxFailures.set(10)
        failOnPassedAfterRetry.set(false)
    }
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    //https://mvnrepository.com/artifact/com.android.tools.build/gradle?repo=google
    implementation("com.android.tools.build:gradle:7.1.1")

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")

    implementation("com.google.dagger:hilt-android-gradle-plugin:2.40.5")

    //URL: https://developers.google.com/android/guides/google-services-plugin
    implementation("com.google.gms:google-services:4.3.10")

    implementation("com.google.firebase:firebase-crashlytics-gradle:2.8.1")
}