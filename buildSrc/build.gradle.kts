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
    implementation(libs.agp)

    //Must match kotlinVersion from Project.kt
    implementation(libs.kotlin.gradle)

    implementation(libs.hilt.gradle)

    //URL: https://developers.google.com/android/guides/google-services-plugin
    implementation(libs.google.services)

    implementation(libs.firebase.crashlytics.gradle)

    implementation(libs.detekt.gradle)

    // Make version catalog available in precompiled scripts
    // https://github.com/gradle/gradle/issues/15383#issuecomment-1567461389
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
