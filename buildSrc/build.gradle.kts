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
    implementation("com.android.tools.build:gradle:7.4.0-rc01")

    //https://kotlinlang.org/docs/releases.html#release-details
    // Must match kotlinVersion from dependencies.kt
    val kotlinVersion = "1.7.20"
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation(kotlin("serialization", version = kotlinVersion))

    //https://developer.android.com/training/dependency-injection/hilt-android
    // Must match hiltVersion from dependencies.kt
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.44")

    //URL: https://developers.google.com/android/guides/google-services-plugin
    implementation("com.google.gms:google-services:4.3.13")

    //https://www.mongodb.com/docs/realm/sdk/kotlin/install/android/
    // Must match Versions.realm from dependencies.kt
//    implementation("io.realm.kotlin:gradle-plugin:1.0.2")

    implementation("com.google.firebase:firebase-crashlytics-gradle:2.9.1")
}