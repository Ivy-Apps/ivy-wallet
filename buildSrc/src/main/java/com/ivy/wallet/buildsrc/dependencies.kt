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

fun DependencyScope.dependencies() {
    dep(::classpath, "com.android.tools.build:gradle:7.0.0")

    val workVersion = "2.7.1"
    val arrowVersion = "1.0.1"


    group("Kotlin", version = "1.5.31") {
        //URL: https://kotlinlang.org/docs/releases.html#release-details
        dep(::implementation, "org.jetbrains.kotlin:kotlin-stdlib:$version")
        dep(::classpath, "org.jetbrains.kotlin:kotlin-gradle-plugin:$version")
        dep(::classpath, "org.jetbrains.kotlin:kotlin-android-extensions:$version")
    }


    group("Compose", version = "1.0.5") {
        //URL: https://developer.android.com/jetpack/androidx/releases/compose
        dep(::implementation, "androidx.compose.ui:ui:$version")
        dep(::implementation, "androidx.compose.foundation:foundation:$version")
        dep(::implementation, "androidx.compose.animation:animation:$version")
        dep(::implementation, "androidx.compose.material:material:$version")
        dep(::implementation, "androidx.compose.material:material-icons-extended:$version")
        dep(::implementation, "androidx.compose.runtime:runtime-livedata:$version")
        dep(::implementation, "androidx.compose.ui:ui-tooling:$version")

        //URL: https://developer.android.com/jetpack/androidx/releases/activity
        dep(::implementation, "androidx.activity:activity-compose:1.3.0-alpha07")

        //URL: https://developer.android.com/jetpack/androidx/releases/lifecycle
        dep(::implementation, "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha05")

        group("Accompanist", version = "0.15.0") {
            //URL: https://github.com/google/accompanist
            dep(::implementation, "com.google.accompanist:accompanist-coil:$version")
            dep(::implementation, "com.google.accompanist:accompanist-insets:$version")
        }

        group("Testing", version = version) {
            //https://developer.android.com/jetpack/compose/testing#setup

            // Test rules and transitive dependencies:
            dep(::testImplementation, "androidx.compose.ui:ui-test-junit4:$version")

            // Needed for createComposeRule, but not createAndroidComposeRule:
            dep(::androidTestImplementation, "androidx.compose.ui:ui-test-manifest:$version")
        }
    }


    group("Google") {
        //URL: https://developers.google.com/android/guides/google-services-plugin
        dep(::classpath, "com.google.gms:google-services:4.3.10")

        //URL: https://mvnrepository.com/artifact/com.google.android.gms/play-services-auth
        dep(::implementation, "com.google.android.gms:play-services-auth:19.2.0")

        //URL: https://developer.android.com/google/play/billing/getting-ready
        dep(::implementation, "com.android.billingclient:billing-ktx:4.0.0")

        //In-App Reviews SDK
        dep(::implementation, "com.google.android.play:core:1.10.0")
        dep(::implementation, "com.google.android.play:core-ktx:1.8.1")
    }


    group("Firebase") {
        dep(::classpath, "com.google.firebase:firebase-crashlytics-gradle:2.4.1")
        dep(::implementation, "com.google.firebase:firebase-crashlytics:17.3.0")
        dep(::implementation, "com.google.firebase:firebase-analytics:18.0.0")
        dep(::implementation, "com.google.firebase:firebase-messaging:21.0.0")
    }

    group("Hilt") {
        //URL: https://developer.android.com/training/dependency-injection/hilt-android
        val hiltVersion = "2.37"
        val versionX = "1.0.0-alpha03"

        dep(::classpath, "com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
        dep(::implementation, "com.google.dagger:hilt-android:$hiltVersion")
        dep(::kapt, "com.google.dagger:hilt-android-compiler:$hiltVersion")


        //URL: https://mvnrepository.com/artifact/androidx.hilt/hilt-lifecycle-viewmodel?repo=google
        dep(::implementation, "androidx.hilt:hilt-lifecycle-viewmodel:$versionX")
        dep(::kapt, "androidx.hilt:hilt-compiler:$versionX")

        //URL: https://developer.android.com/training/dependency-injection/hilt-jetpack#workmanager
        dep(::implementation, "androidx.hilt:hilt-work:$versionX")

        group("Testing", version = hiltVersion) {
            //https://developer.android.com/training/dependency-injection/hilt-testing

            dep(::androidTestImplementation, "com.google.dagger:hilt-android-testing:$version")
            dep(::kaptAndroidTest, "com.google.dagger:hilt-android-compiler:$version")
            dep(::implementation, "androidx.test:runner:1.4.0")
            dep(::implementation, "androidx.work:work-testing:$workVersion")
        }
    }


    group("Room", version = "2.4.0-alpha03") {
        //URL: https://developer.android.com/jetpack/androidx/releases/room
        dep(::implementation, "androidx.room:room-runtime:$version")
        dep(::kapt, "androidx.room:room-compiler:$version")
        dep(::implementation, "androidx.room:room-ktx:$version")
    }


    group("REST") {
        //URL: https://github.com/square/retrofit
        val retrofitVersion = "2.9.0"
        dep(::implementation, "com.squareup.retrofit2:retrofit:$retrofitVersion")
        dep(::implementation, "com.squareup.retrofit2:converter-gson:$retrofitVersion")

        //URL: https://github.com/google/gson
        dep(::implementation, "com.google.code.gson:gson:2.8.7")


        //URL: https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor
        dep(::implementation, "com.squareup.okhttp3:logging-interceptor:4.9.1")
    }


    group("Lifecycle", version = "2.3.1") {
        //URL: https://developer.android.com/jetpack/androidx/releases/lifecycle
        dep(::implementation, "androidx.lifecycle:lifecycle-livedata-ktx:$version")
        dep(::implementation, "androidx.lifecycle:lifecycle-viewmodel-ktx:$version")
        dep(::implementation, "androidx.lifecycle:lifecycle-viewmodel-savedstate:$version")
        dep(::implementation, "androidx.lifecycle:lifecycle-runtime-ktx:$version")
        dep(::kapt, "androidx.lifecycle:lifecycle-compiler:$version")
    }

    group("AndroidX") {
        //https://developer.android.com/jetpack/androidx/releases/appcompat
        dep(::implementation, "androidx.appcompat:appcompat:1.3.0")
        dep(::implementation, "androidx.constraintlayout:constraintlayout:2.0.4")

        //URL: https://developer.android.com/jetpack/androidx/releases/core
        dep(::implementation, "androidx.core:core-ktx:1.5.0")

        //URL: https://developer.android.com/jetpack/androidx/releases/work
        dep(::implementation, "androidx.work:work-runtime-ktx:$workVersion")

        dep(::implementation, "androidx.biometric:biometric:1.1.0")

        //URL: https://developer.android.com/jetpack/androidx/releases/recyclerview
        dep(::implementation, "androidx.recyclerview:recyclerview:1.2.1")

        dep(::implementation, "androidx.webkit:webkit:1.4.0")
    }


    group("Coroutines", version = "1.5.0") {
        //URL: https://github.com/Kotlin/kotlinx.coroutines
        dep(::implementation, "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version")
        dep(::implementation, "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version")

        //URL: https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-play-services
        dep(::implementation, "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.4.3")
    }


    group("Third Party") {
        //URL: https://github.com/airbnb/lottie-android
        dep(::implementation, "com.airbnb.android:lottie:3.7.0")

        //URL: https://github.com/jeziellago/compose-markdown
        dep(::implementation, "com.github.jeziellago:compose-markdown:0.2.6")

        //URL: https://github.com/JakeWharton/timber/releases
        dep(::implementation, "com.jakewharton.timber:timber:4.7.1")

        //URL: https://github.com/greenrobot/EventBus/releases
        dep(::implementation, "org.greenrobot:eventbus:3.2.0")

        //URL: https://github.com/notKamui/Keval - evaluate math expressions (calculator)
        dep(::implementation, "com.notkamui.libs:keval:0.7.5")
    }


    group("Java") {
        dep(::implementation, "com.opencsv:opencsv:5.5")
        dep(::implementation, "org.apache.commons:commons-lang3:3.12.0")
    }


    group("Property-based Testing") {
        dep(::testImplementation, "io.kotest:kotest-property:5.1.0")
        dep(
            ::testImplementation,
            "io.kotest.extensions:kotest-property-arrow:$arrowVersion"
        )
    }


    group("Arrow", version = arrowVersion) {
        //Functional programming with Kotlin
        dep(::platformBom, "io.arrow-kt:arrow-stack:$version")
        dep(::implementation, "io.arrow-kt:arrow-core")
        dep(::implementation, "io.arrow-kt:arrow-fx-coroutines")
        dep(::implementation, "io.arrow-kt:arrow-fx-stm")
    }
}

object Project {
    //Version
    const val versionName = "2.3.4-halley"
    const val versionCode = 94

    //Compile SDK & Build Tools
    const val compileSdkVersion = 31

    //App
    const val applicationId = "com.ivy.wallet"
    const val minSdk = 28
    const val targetSdk = 30
}