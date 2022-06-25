/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivy.wallet.buildsrc

import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project


object Project {
    //Version
    const val versionName = "4.3.3"
    const val versionCode = 117

    //Compile SDK & Build Tools
    const val compileSdkVersion = 31

    //App
    const val applicationId = "com.ivy.wallet"
    const val minSdk = 28
    const val targetSdk = 30
}

object GlobalVersions {
    const val compose = "1.1.1"
    const val kotlinVersion = "1.6.20"
}

/**
 * @param kotlinVersion must also be updated in buildSrc gradle
 */
fun DependencyHandler.appModuleDependencies(
    kotlinVersion: String = GlobalVersions.kotlinVersion
) {
    implementation(project(":ivy-design"))

    implementation("com.github.ILIYANGERMANOV:ivy-frp:0.9.5")

    Kotlin(version = kotlinVersion)
    Coroutines(version = "1.5.0")
    FunctionalProgramming(
        arrowVersion = "1.0.1",
        kotestVersion = "5.1.0",
        kotlinVersion = kotlinVersion
    )

    Compose(version = GlobalVersions.compose)

    Google()
    Firebase()

    Hilt(
        hiltVersion = "2.38.1",
        versionX = "1.0.0"
    )
    RoomDB(version = "2.4.0-alpha03")

    Networking(retrofitVersion = "2.9.0")

    Lifecycle(version = "2.3.1")
    AndroidX()

    DataStore()

    ThirdParty()
}

fun DependencyHandler.ivyDesignModuleDependencies(
    kotlinVersion: String = GlobalVersions.kotlinVersion
) {
    Kotlin(version = kotlinVersion)
    Coroutines(version = "1.5.0")
    FunctionalProgramming(
        arrowVersion = "1.0.1",
        kotestVersion = "5.1.0",
        kotlinVersion = kotlinVersion
    )

    Compose(version = GlobalVersions.compose)

    AndroidX()
    Lifecycle(version = "2.3.1")
}
//---------------------------------------------------------------------------------

fun DependencyHandler.DataStore() {
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}

/**
 * Kotlin STD lib
 * https://kotlinlang.org/docs/releases.html#release-details
 */
fun DependencyHandler.Kotlin(version: String) {
    //URL: https://kotlinlang.org/docs/releases.html#release-details
    //WARNING: Version is also updated from buildSrc
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$version")
}

fun DependencyHandler.Compose(version: String) {
    //URL: https://developer.android.com/jetpack/androidx/releases/compose
    implementation("androidx.compose.ui:ui:$version")
    implementation("androidx.compose.foundation:foundation:$version")
    implementation("androidx.compose.animation:animation:$version")
    implementation("androidx.compose.material:material:$version")
    implementation("androidx.compose.material:material-icons-extended:$version")
    implementation("androidx.compose.runtime:runtime-livedata:$version")
    implementation("androidx.compose.ui:ui-tooling:$version")

    //URL: https://developer.android.com/jetpack/androidx/releases/activity
    implementation("androidx.activity:activity-compose:1.4.0")

    //URL: https://developer.android.com/jetpack/androidx/releases/lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha05")

    // Jetpack Glance (Compose Widgets)
    implementation ("androidx.glance:glance-appwidget:1.0.0-alpha03")

    Accompanist(version = "0.15.0")

    Coil()

    ComposeTesting(version = version)
}

/**
 *  Compose Window Insets + extras
 *  https://github.com/google/accompanist
 */
fun DependencyHandler.Accompanist(version: String) {
    implementation("com.google.accompanist:accompanist-coil:$version")
    implementation("com.google.accompanist:accompanist-insets:$version")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.24.4-alpha")
}

fun DependencyHandler.Coil() {
    implementation("io.coil-kt:coil-compose:2.0.0")
}

/**
 * Required for running Compose UI tests
 * https://developer.android.com/jetpack/compose/testing#setup
 */
fun DependencyHandler.ComposeTesting(version: String) {
    //THIS IS NOT RIGHT: Implementation for IdlingResource access on both Debug & Release
    //Without having this dependency "lintRelease" fails
    implementation("androidx.compose.ui:ui-test-junit4:$version")

    // Needed for createComposeRule, but not createAndroidComposeRule:
    androidTestImplementation("androidx.compose.ui:ui-test-manifest:$version")
}

fun DependencyHandler.Google() {
    //URL: https://mvnrepository.com/artifact/com.google.android.gms/play-services-auth
    implementation("com.google.android.gms:play-services-auth:19.2.0")

    //URL: https://developer.android.com/google/play/billing/getting-ready
    implementation("com.android.billingclient:billing-ktx:4.0.0")

    //In-App Reviews SDK
    implementation("com.google.android.play:core:1.10.0")
    implementation("com.google.android.play:core-ktx:1.8.1")
}

fun DependencyHandler.Firebase() {
    implementation("com.google.firebase:firebase-crashlytics:17.3.0")
    implementation("com.google.firebase:firebase-analytics:18.0.0")
    implementation("com.google.firebase:firebase-messaging:21.0.0")
}

/**
 * Hilt DI
 * https://developer.android.com/training/dependency-injection/hilt-android
 */
fun DependencyHandler.Hilt(
    hiltVersion: String,
    versionX: String
) {
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")

    //URL: https://mvnrepository.com/artifact/androidx.hilt/hilt-lifecycle-viewmodel?repo=google
//    implementation("androidx.hilt:hilt-lifecycle-viewmodel:$versionX")
    kapt("androidx.hilt:hilt-compiler:$versionX")

    //URL: https://developer.android.com/training/dependency-injection/hilt-jetpack#workmanager
    implementation("androidx.hilt:hilt-work:$versionX")

    HiltTesting(version = hiltVersion)
}

fun DependencyHandler.HiltTesting(
    version: String
) {
    androidTestImplementation("com.google.dagger:hilt-android-testing:$version")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:$version")
    implementation("androidx.test:runner:1.4.0")
}

/**
 * https://developer.android.com/jetpack/androidx/releases/room
 */
fun DependencyHandler.RoomDB(
    version: String = "2.4.0-alpha03"
) {
    implementation("androidx.room:room-runtime:$version")
    kapt("androidx.room:room-compiler:$version")
    implementation("androidx.room:room-ktx:$version")
}

/**
 * REST
 */
fun DependencyHandler.Networking(
    retrofitVersion: String = "2.9.0"
) {
    //URL: https://github.com/square/retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    //URL: https://github.com/google/gson
    implementation("com.google.code.gson:gson:2.8.7")


    //URL: https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
}

/**
 * Jetpack Compose Lifecycle
 * https://developer.android.com/jetpack/androidx/releases/lifecycle
 */
fun DependencyHandler.Lifecycle(
    version: String = "2.3.1"
) {
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$version")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$version")
    kapt("androidx.lifecycle:lifecycle-compiler:$version")
}

fun DependencyHandler.AndroidX() {
    //https://developer.android.com/jetpack/androidx/releases/appcompat
    implementation("androidx.appcompat:appcompat:1.4.1")

    //URL: https://developer.android.com/jetpack/androidx/releases/core
    implementation("androidx.core:core-ktx:1.7.0")

    //URL: https://developer.android.com/jetpack/androidx/releases/work
    val workVersion = "2.7.1"
    implementation("androidx.work:work-runtime-ktx:$workVersion")
    implementation("androidx.work:work-testing:$workVersion")

    implementation("androidx.biometric:biometric:1.1.0")

    //URL: https://developer.android.com/jetpack/androidx/releases/recyclerview
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    implementation("androidx.webkit:webkit:1.4.0")
}

fun DependencyHandler.Coroutines(
    version: String = "1.6.0"
) {
    //URL: https://github.com/Kotlin/kotlinx.coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$version")

    //URL: https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-play-services
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$version")

    //URL: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/
    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$version")
}

fun DependencyHandler.ThirdParty() {
    //URL: https://github.com/airbnb/lottie-android
    implementation("com.airbnb.android:lottie:3.7.0")

    //URL: https://github.com/jeziellago/compose-markdown
    implementation("com.github.jeziellago:compose-markdown:0.2.6")

    //URL: https://github.com/JakeWharton/timber/releases
    implementation("com.jakewharton.timber:timber:4.7.1")

    //URL: https://github.com/greenrobot/EventBus/releases
    implementation("org.greenrobot:eventbus:3.2.0")

    //URL: https://github.com/notKamui/Keval - evaluate math expressions (calculator)
    implementation("com.notkamui.libs:keval:0.8.0")

    implementation("com.opencsv:opencsv:5.5")
    implementation("org.apache.commons:commons-lang3:3.12.0")
}

fun DependencyHandler.FunctionalProgramming(
    arrowVersion: String = "1.0.1",
    kotestVersion: String = "5.1.0",
    kotlinVersion: String
) {
    Arrow(version = arrowVersion)

    Kotest(
        version = kotestVersion,
        arrowVersion = arrowVersion,
        kotlinVersion = kotlinVersion
    )
}

/**
 * Functional Programming with Kotlin
 */
fun DependencyHandler.Arrow(
    version: String
) {
    implementation(platform("io.arrow-kt:arrow-stack:$version"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-fx-coroutines")
    implementation("io.arrow-kt:arrow-fx-stm")
}

/**
 * Kotlin Property-based testing
 */
fun DependencyHandler.Kotest(
    version: String,
    arrowVersion: String,
    kotlinVersion: String
) {
    //junit5 is required!
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("io.kotest:kotest-runner-junit5:$version")
    testImplementation("io.kotest:kotest-assertions-core:$version")
    testImplementation("io.kotest:kotest-property:$version")

    //otherwise Kotest doesn't work...
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")


    testImplementation("io.kotest.extensions:kotest-property-arrow:$arrowVersion")
}