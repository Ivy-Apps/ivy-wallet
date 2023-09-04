plugins {
    `kotlin-dsl`
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
    // https://mvnrepository.com/artifact/com.android.tools.build/gradle?repo=google
    implementation(libs.android.gradle.plugin)

    // Must match kotlinVersion from Project.kt
    implementation(libs.kotlin.gradle.plugin)

    implementation(libs.hilt.gradle.plugin)

    // URL: https://developers.google.com/android/guides/google-services-plugin
    implementation(libs.google.services)

    implementation(libs.firebase.crashlytics.gradle.plugin)

    implementation(libs.detekt.gradle.plugin)

    // Make version catalog available in precompiled scripts
    // https://github.com/gradle/gradle/issues/15383#issuecomment-1567461389
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
