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


object Project {
    //Version
    const val versionName = "4.4.4"
    const val versionCode = 144

    //Compile SDK & Build Tools
    const val compileSdkVersion = 34

    //App
    const val applicationId = "com.ivy.wallet"
    const val minSdk = 28
    const val targetSdk = 34
}

/**
 * @param kotlinVersion must also be updated in buildSrc gradle
 */
fun DependencyHandler.appModuleDependencies() {
//    implementation(project(":ivy-design"))

//    implementation("com.github.ILIYANGERMANOV:ivy-frp:0.9.5")

//    Coroutines(version = "1.5.0")
//    FunctionalProgramming(
//            arrowVersion = "1.0.1",
//            kotestVersion = "5.1.0",
//            kotlinVersion = kotlinVersion
//    )

//    Compose(version = GlobalVersions.compose)


//    Hilt(
//        hiltVersion = "2.47",
//        versionX = "1.0.0"
//    )

    Lifecycle(version = "2.3.1")
    AndroidX()

    ThirdParty()
}

fun DependencyHandler.ivyDesignModuleDependencies() {
//    Coroutines(version = "1.5.0")
//    FunctionalProgramming(
//            arrowVersion = "1.0.1",
//            kotestVersion = "5.1.0",
//            kotlinVersion = kotlinVersion
//    )

//    Compose(version = GlobalVersions.compose)

    AndroidX()
    Lifecycle(version = "2.3.1")
}
//---------------------------------------------------------------------------------


/**
 * Jetpack Compose Lifecycle
 * https://developer.android.com/jetpack/androidx/releases/lifecycle
 */
fun DependencyHandler.Lifecycle(
    version: String = "2.6.1"
) {
    // https://developer.android.com/jetpack/androidx/releases/lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$version")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$version")
    kapt("androidx.lifecycle:lifecycle-compiler:$version")
}

fun DependencyHandler.AndroidX() {
    //https://developer.android.com/jetpack/androidx/releases/appcompat
    implementation("androidx.appcompat:appcompat:1.6.1")

    //URL: https://developer.android.com/jetpack/androidx/releases/core
    implementation("androidx.core:core-ktx:1.10.1")

    //URL: https://developer.android.com/jetpack/androidx/releases/work
    val workVersion = "2.7.1"
    implementation("androidx.work:work-runtime-ktx:$workVersion")
    implementation("androidx.work:work-testing:$workVersion")

    implementation("androidx.biometric:biometric:1.1.0")

    //URL: https://developer.android.com/jetpack/androidx/releases/recyclerview
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    implementation("androidx.webkit:webkit:1.4.0")
}


fun DependencyHandler.ThirdParty() {
    //URL: https://github.com/airbnb/lottie-android
    implementation("com.airbnb.android:lottie:3.7.0")

    //URL: https://github.com/jeziellago/compose-markdown
    implementation("com.github.jeziellago:compose-markdown:0.2.6")

    //URL: https://github.com/JakeWharton/timber/releases
    implementation("com.jakewharton.timber:timber:4.7.1")

    //URL: https://github.com/greenrobot/EventBus/releases
    implementation("org.greenrobot:eventbus:3.3.1")

    //URL: https://github.com/notKamui/Keval - evaluate math expressions (calculator)
    implementation("com.notkamui.libs:keval:0.9.0")

    implementation("com.opencsv:opencsv:5.5")
    implementation("org.apache.commons:commons-lang3:3.12.0")
}