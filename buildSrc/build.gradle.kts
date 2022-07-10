plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    //https://mvnrepository.com/artifact/com.android.tools.build/gradle?repo=google
    implementation("com.android.tools.build:gradle:7.3.0-beta04")

    //https://kotlinlang.org/docs/releases.html#release-details
    //Must match kotlinVersion from dependencies.kt
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")

    //https://developer.android.com/training/dependency-injection/hilt-android
    //Must match hiltVersion from dependencies.kt
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.42")

    //URL: https://developers.google.com/android/guides/google-services-plugin
    implementation("com.google.gms:google-services:4.3.13")

    implementation("com.google.firebase:firebase-crashlytics-gradle:2.9.1")
}