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
    const val compileSdkVersion = 31

    //App
    const val applicationId = "com.ivy.wallet"
    const val minSdk = 28
    const val targetSdk = 30
}

object Versions {
    //TODO: Copy comments URLs and add them here

    //URL: https://kotlinlang.org/docs/releases.html#release-details
    //WARNING: Version is also updated from buildSrc
    const val kotlin = "1.6.10"
    const val coroutines = "1.6.3"

    //URL: https://developer.android.com/jetpack/androidx/releases/compose
    const val compose = "1.1.1"
    const val composeActivity = "1.4.0"
    const val composeViewModel = "1.0.0-alpha05"
    const val composeGlance = "1.0.0-alpha03"
    const val composeAccompanist = "0.15.0"
    const val composeCoil = "2.0.0"

    const val arrow: String = "1.0.1"
    const val kotest: String = "5.1.0"
    const val junitJupiter: String = "5.8.2"
    const val hilt = "2.38.1"
    const val hiltX = "1.0.0"
    const val androidXTestRunner = "1.4.0"

    const val appCompat = "1.4.1"
    const val coreKtx = "1.7.0" //androidx
    const val workVersion = "2.7.1"
    const val biometric = "1.1.0"
    const val recyclerView = "1.2.1"
    const val webkit = "1.4.0"

    const val lifecycle = "2.3.1"
}

fun DependencyHandler.IvyFRP(
    api: Boolean = false
) {
    dependency("com.github.ILIYANGERMANOV:ivy-frp:0.9.5", api = api)
}

fun DependencyHandler.DataStore() {
    implementation("androidx.datastore:datastore-preferences:1.0.0")
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
    dependency("androidx.compose.foundation:foundation:$version", api = api)
    dependency("androidx.compose.animation:animation:$version", api = api)
    dependency("androidx.compose.material:material:$version", api = api)
    dependency("androidx.compose.material:material-icons-extended:$version", api = api)
    dependency("androidx.compose.runtime:runtime-livedata:$version", api = api)
    dependency("androidx.compose.ui:ui-tooling:$version", api = api)

    //URL: https://developer.android.com/jetpack/androidx/releases/activity
    dependency(
        "androidx.activity:activity-compose:${Versions.composeActivity}", api = api
    )

    //URL: https://developer.android.com/jetpack/androidx/releases/lifecycle
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
    dependency(
        dependency = "com.google.accompanist:accompanist-coil:${Versions.composeAccompanist}",
        api = api
    )
    dependency(
        dependency = "com.google.accompanist:accompanist-insets:${Versions.composeAccompanist}",
        api = api
    )

    //TODO: Review what is this? Why is hard-coded to that version?
    dependency(
        "com.google.accompanist:accompanist-systemuicontroller:0.24.4-alpha", api = api
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

    //URL: https://developer.android.com/google/play/billing/getting-ready
    implementation("com.android.billingclient:billing-ktx:4.0.0")

    //URL: https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-play-services
    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.3}"
    )

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
fun DependencyHandler.Hilt() {
    val api = true
    dependency("com.google.dagger:hilt-android:${Versions.hilt}", api = api)
    kapt("com.google.dagger:hilt-android-compiler:${Versions.hilt}")

    //URL: https://mvnrepository.com/artifact/androidx.hilt/hilt-lifecycle-viewmodel?repo=google
//    implementation("androidx.hilt:hilt-lifecycle-viewmodel:$versionX")
    kapt("androidx.hilt:hilt-compiler:${Versions.hiltX}")

    //URL: https://developer.android.com/training/dependency-injection/hilt-jetpack#workmanager
    dependency("androidx.hilt:hilt-work:${Versions.hiltX}", api = api)

    HiltTesting()
}

fun DependencyHandler.HiltTesting() {
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

    //URL: https://developer.android.com/jetpack/androidx/releases/work
    dependency("androidx.work:work-runtime-ktx:${Versions.workVersion}", api = api)
    dependency("androidx.work:work-testing:${Versions.workVersion}", api = api)

    dependency("androidx.biometric:biometric:${Versions.biometric}", api = api)

    //URL: https://developer.android.com/jetpack/androidx/releases/recyclerview
    dependency("androidx.recyclerview:recyclerview:${Versions.recyclerView}", api = api)

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

    //URL: https://github.com/JakeWharton/timber/releases
    implementation("com.jakewharton.timber:timber:4.7.1")

    //URL: https://github.com/greenrobot/EventBus/releases
    implementation("org.greenrobot:eventbus:3.2.0")

    //URL: https://github.com/notKamui/Keval - evaluate math expressions (calculator)
    implementation("com.notkamui.libs:keval:0.8.0")

    implementation("com.opencsv:opencsv:5.5")
    implementation("org.apache.commons:commons-lang3:3.12.0")
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