plugins {
    `kotlin-dsl`
    id("org.gradle.test-retry") version "1.2.0"
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

repositories {
    google()
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    //https://mvnrepository.com/artifact/com.android.tools.build/gradle?repo=google
    implementation("com.android.tools.build:gradle:8.2.0-beta01")

    //Must match kotlinVersion from dependencies.kt
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")

    implementation("com.google.dagger:hilt-android-gradle-plugin:2.47")

    //URL: https://developers.google.com/android/guides/google-services-plugin
    implementation("com.google.gms:google-services:4.3.15")

    implementation("com.google.firebase:firebase-crashlytics-gradle:2.9.8")

    implementation(libs.detekt.gradle)

    // Make version catalog available in precompiled scripts
    // https://github.com/gradle/gradle/issues/15383#issuecomment-1567461389
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
