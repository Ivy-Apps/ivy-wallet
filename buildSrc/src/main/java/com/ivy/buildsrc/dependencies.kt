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

package com.ivy.buildsrc

import org.gradle.api.artifacts.dsl.DependencyHandler


object Project {
    //Version
    const val versionName = "4.3.3"
    const val versionCode = 117

    //Compile SDK & Build Tools
    const val compileSdkVersion = 32

    //App
    const val applicationId = "com.ivy.wallet"
    const val minSdk = 28
    const val targetSdk = 30
}

object Versions {
    //TODO: Copy comments URLs and add them here

    //URL: https://kotlinlang.org/docs/releases.html#release-details
    //WARNING: Version is also updated from buildSrc
    const val kotlin = "1.7.0"
    const val coroutines = "1.6.3"

    //https://developer.android.com/jetpack/androidx/releases/compose
    const val compose = "1.2.0-rc03"

    //https://developer.android.com/jetpack/androidx/releases/compose-compiler
    const val composeCompilerVersion = "1.2.0"

    //https://developer.android.com/jetpack/androidx/releases/compose-foundation
    const val composeFoundation = "1.2.0-rc03"

    //https://developer.android.com/jetpack/androidx/releases/activity
    const val composeActivity = "1.5.0"

    //https://developer.android.com/jetpack/androidx/releases/lifecycle
    const val composeViewModel = "2.6.0-alpha01"
    //https://developer.android.com/jetpack/androidx/releases/glance
    const val composeGlance = "1.0.0-alpha03"

    //Set status bar color
    //https://google.github.io/accompanist/systemuicontroller/
    const val composeAccompanistUIController = "0.24.13-rc"

    //https://coil-kt.github.io/coil/compose/
    const val composeCoil = "2.1.0"

    const val arrow: String = "1.0.1"
    const val kotest: String = "5.1.0"
    const val junitJupiter: String = "5.8.2"

    //https://developer.android.com/training/dependency-injection/hilt-android
    //WARNING: Update hilt gradle plugin from buildSrc
    const val hilt = "2.42"
    //https://mvnrepository.com/artifact/androidx.hilt/hilt-compiler?repo=google
    const val hiltX = "1.0.0"

    const val androidXTestRunner = "1.4.0"

    //https://developer.android.com/jetpack/androidx/releases/appcompat
    const val appCompat = "1.4.2"
    //https://developer.android.com/jetpack/androidx/releases/core
    const val coreKtx = "1.9.0-alpha05"
    //https://developer.android.com/jetpack/androidx/releases/work
    const val workVersion = "2.8.0-alpha02"
    //https://developer.android.com/jetpack/androidx/releases/biometric
    const val biometric = "1.2.0-alpha04"
    //https://developer.android.com/jetpack/androidx/releases/recyclerview
    const val recyclerView = "1.3.0-beta01"
    //https://developer.android.com/jetpack/androidx/releases/webkit
    const val webkit = "1.5.0-beta01"

    //https://developer.android.com/jetpack/androidx/releases/lifecycle
    const val lifecycle = "2.6.0-alpha01"

    //https://developer.android.com/jetpack/androidx/releases/room
    const val room = "2.4.2"

    //https://github.com/square/retrofit
    const val retrofit = "2.9.0"

    //https://github.com/google/gson
    const val gson = "2.8.7"

    //https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor
    const val okhttpLogging = "4.9.1"

    //https://github.com/JakeWharton/timber/releases
    const val timber = "4.7.1"

    //https://github.com/greenrobot/EventBus/releases
    const val eventBus = "3.2.0"

    //https://developer.android.com/jetpack/androidx/releases/datastore
    const val dataStore = "1.0.0"


    //https://developer.android.com/google/play/billing/getting-ready
    const val googleBilling = "4.0.0"
}

fun DependencyHandler.IvyFRP(
    api: Boolean = false
) {
    dependency("com.github.ILIYANGERMANOV:ivy-frp:0.9.5", api = api)
}

fun DependencyHandler.DataStore(api: Boolean) {
    dependency("androidx.datastore:datastore-preferences:1.0.0", api = api)
}

/**
 * Kotlin STD lib
 * https://kotlinlang.org/docs/releases.html#release-details
 */
fun DependencyHandler.Kotlin(api: Boolean) {
    //URL: https://kotlinlang.org/docs/releases.html#release-details
    //WARNING: Version is also updated from buildSrc
    dependency("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}", api = api)
}

fun DependencyHandler.Compose(api: Boolean) {
    val version = Versions.compose
    //URL: https://developer.android.com/jetpack/androidx/releases/compose
    dependency("androidx.compose.ui:ui:$version", api = api)
    dependency(
        "androidx.compose.foundation:foundation:${Versions.composeFoundation}",
        api = api
    )
    dependency(
        "androidx.compose.foundation:foundation-layout:${Versions.composeFoundation}",
        api = api
    )
    dependency("androidx.compose.animation:animation:$version", api = api)
    dependency("androidx.compose.material:material:$version", api = api)
    dependency("androidx.compose.material:material-icons-extended:$version", api = api)
    dependency("androidx.compose.runtime:runtime-livedata:$version", api = api)
    dependency("androidx.compose.ui:ui-tooling:$version", api = api)

    dependency(
        "androidx.activity:activity-compose:${Versions.composeActivity}", api = api
    )

    dependency(
        "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.composeViewModel}",
        api = api
    )

    // Jetpack Glance (Compose Widgets)
    dependency("androidx.glance:glance-appwidget:${Versions.composeGlance}", api = api)

    Accompanist(api = api)

    Coil(api = api)

    ComposeTesting(api = api)
}

/**
 *  Compose Window Insets + extras
 *  https://github.com/google/accompanist
 */
fun DependencyHandler.Accompanist(api: Boolean) {
    //Set status bar color
    //https://google.github.io/accompanist/systemuicontroller/
    dependency(
        "com.google.accompanist:accompanist-systemuicontroller:${Versions.composeAccompanistUIController}",
        api = api
    )
}

fun DependencyHandler.Coil(api: Boolean) {
    dependency("io.coil-kt:coil-compose:${Versions.composeCoil}", api = api)
}

/**
 * Required for running Compose UI tests
 * https://developer.android.com/jetpack/compose/testing#setup
 */
fun DependencyHandler.ComposeTesting(api: Boolean) {
    //THIS IS NOT RIGHT: Implementation for IdlingResource access on both Debug & Release
    //Without having this dependency "lintRelease" fails
    //TODO: Fix that
    dependency("androidx.compose.ui:ui-test-junit4:${Versions.compose}", api = api)

    // Needed for createComposeRule, but not createAndroidComposeRule:
    androidTestDependency(
        "androidx.compose.ui:ui-test-manifest:${Versions.compose}", api = api
    )
}

fun DependencyHandler.Google() {
    //URL: https://mvnrepository.com/artifact/com.google.android.gms/play-services-auth
    implementation("com.google.android.gms:play-services-auth:19.2.0")

    //URL: https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-play-services
    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.3}"
    )

    Billing(api = false)

    //In-App Reviews SDK
    implementation("com.google.android.play:core:1.10.0")
    implementation("com.google.android.play:core-ktx:1.8.1")
}

fun DependencyHandler.Billing(api: Boolean) {
    //https://developer.android.com/google/play/billing/getting-ready
    dependency("com.android.billingclient:billing-ktx:${Versions.googleBilling}", api = api)
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
fun DependencyHandler.Hilt() {
    val api = true
    dependency("com.google.dagger:hilt-android:${Versions.hilt}", api = api)
    kapt("com.google.dagger:hilt-android-compiler:${Versions.hilt}")

    kapt("androidx.hilt:hilt-compiler:${Versions.hiltX}")

    //URL: https://developer.android.com/training/dependency-injection/hilt-jetpack#workmanager
    dependency("androidx.hilt:hilt-work:${Versions.hiltX}", api = api)

    HiltTesting()
}

private fun DependencyHandler.HiltTesting() {
    val api = true
    androidTestDependency(
        "com.google.dagger:hilt-android-testing:${Versions.hilt}", api = api
    )
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:${Versions.hilt}")

    //TODO: Investigate why this is not test dependency
    dependency(
        "androidx.test:runner:${Versions.androidXTestRunner}", api = api
    )
}

/**
 * https://developer.android.com/jetpack/androidx/releases/room
 */
fun DependencyHandler.RoomDB(api: Boolean) {
    dependency("androidx.room:room-runtime:${Versions.room}", api = api)
    kapt("androidx.room:room-compiler:${Versions.room}")
    dependency("androidx.room:room-ktx:${Versions.room}", api =  api)
}

/**
 * REST
 */
fun DependencyHandler.Networking(api: Boolean) {
    //URL: https://github.com/square/retrofit
    dependency("com.squareup.retrofit2:retrofit:${Versions.retrofit}", api = api)
    dependency("com.squareup.retrofit2:converter-gson:${Versions.retrofit}", api = api)

    Gson(api = api)

    //URL: https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor
    dependency(
        "com.squareup.okhttp3:logging-interceptor:${Versions.okhttpLogging}", api = api
    )
}

fun DependencyHandler.Gson(api: Boolean) {
    //URL: https://github.com/google/gson
    dependency("com.google.code.gson:gson:${Versions.gson}", api = api)
}

/**
 * Jetpack Compose Lifecycle
 * https://developer.android.com/jetpack/androidx/releases/lifecycle
 */
fun DependencyHandler.Lifecycle(
    api: Boolean
) {
    val version = Versions.lifecycle
    dependency("androidx.lifecycle:lifecycle-livedata-ktx:$version", api = api)
    dependency("androidx.lifecycle:lifecycle-viewmodel-ktx:$version", api = api)
    dependency("androidx.lifecycle:lifecycle-viewmodel-savedstate:$version", api = api)
    dependency("androidx.lifecycle:lifecycle-runtime-ktx:$version", api = api)

    //TODO: Warning "kapt" is not transitive!
    kapt("androidx.lifecycle:lifecycle-compiler:$version")
}

fun DependencyHandler.AndroidX(api: Boolean) {
    //https://developer.android.com/jetpack/androidx/releases/appcompat
    dependency("androidx.appcompat:appcompat:${Versions.appCompat}", api = api)

    //URL: https://developer.android.com/jetpack/androidx/releases/core
    dependency("androidx.core:core-ktx:${Versions.coreKtx}", api = api)

    //https://developer.android.com/jetpack/androidx/releases/work
    dependency("androidx.work:work-runtime-ktx:${Versions.workVersion}", api = api)
    dependency("androidx.work:work-testing:${Versions.workVersion}", api = api)

    dependency("androidx.biometric:biometric:${Versions.biometric}", api = api)

    //URL: https://developer.android.com/jetpack/androidx/releases/recyclerview
    dependency("androidx.recyclerview:recyclerview:${Versions.recyclerView}", api = api)

    //https://developer.android.com/jetpack/androidx/releases/webkit
    dependency("androidx.webkit:webkit:${Versions.webkit}", api = api)
}

fun DependencyHandler.Coroutines(
    api: Boolean
) {
    val version = Versions.coroutines
    //URL: https://github.com/Kotlin/kotlinx.coroutines
    dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:$version", api = api)
    dependency("org.jetbrains.kotlinx:kotlinx-coroutines-android:$version", api = api)

    //URL: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/
    androidTestDependency(
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version", api = api
    )
}

fun DependencyHandler.ThirdParty() {
    //URL: https://github.com/airbnb/lottie-android
    implementation("com.airbnb.android:lottie:3.7.0")

    //URL: https://github.com/jeziellago/compose-markdown
    implementation("com.github.jeziellago:compose-markdown:0.2.6")

    EventBus()

    //URL: https://github.com/notKamui/Keval - evaluate math expressions (calculator)
    implementation("com.notkamui.libs:keval:0.8.0")

    OpenCSV()
}

fun DependencyHandler.OpenCSV() {
    implementation("com.opencsv:opencsv:5.5")
    implementation("org.apache.commons:commons-lang3:3.12.0")
}

fun DependencyHandler.EventBus() {
    //URL: https://github.com/greenrobot/EventBus/releases
    implementation("org.greenrobot:eventbus:${Versions.eventBus}")
}

fun DependencyHandler.Timber(api: Boolean) {
    //URL: https://github.com/JakeWharton/timber/releases
    dependency("com.jakewharton.timber:timber:${Versions.timber}", api = api)
}

fun DependencyHandler.FunctionalProgramming(api: Boolean) {
    Arrow(api)

    Kotest(api)
}

/**
 * Functional Programming with Kotlin
 */
fun DependencyHandler.Arrow(
    api: Boolean
) {
    dependency(platform("io.arrow-kt:arrow-stack:${Versions.arrow}"), api = api)
    dependency("io.arrow-kt:arrow-core", api = api)
    dependency("io.arrow-kt:arrow-fx-coroutines", api = api)
    dependency("io.arrow-kt:arrow-fx-stm", api = api)
}

/**
 * Kotlin Property-based testing
 */
fun DependencyHandler.Kotest(api: Boolean) {
    //junit5 is required!
    testDependency("org.junit.jupiter:junit-jupiter:${Versions.junitJupiter}", api = api)
    testDependency("io.kotest:kotest-runner-junit5:${Versions.kotest}", api = api)
    testDependency("io.kotest:kotest-assertions-core:${Versions.kotest}", api = api)
    testDependency("io.kotest:kotest-property:${Versions.kotest}", api = api)

    //otherwise Kotest doesn't work...
    testDependency("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}", api = api)


    testDependency(
        "io.kotest.extensions:kotest-property-arrow:${Versions.arrow}", api = api
    )
}