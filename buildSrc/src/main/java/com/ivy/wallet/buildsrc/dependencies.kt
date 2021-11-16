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

object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.0"

    object Project {
        //Version
        const val versionName = "2.2.3-comet"
        const val versionCode = 81

        //Compile SDK & Build Tools
        const val compileSdkVersion = 30
        const val buildToolsVersion = "30.0.3"

        //App
        const val applicationId = "com.ivy.wallet"
        const val minSdk = 28
        const val targetSdk = 30
    }

    object Kotlin {
        //URL: https://kotlinlang.org/docs/releases.html#release-details
        const val version = "1.5.30"

        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val androidExtensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
    }

    object AndroidX {
        //https://developer.android.com/jetpack/androidx/releases/appcompat
        const val appcompat = "androidx.appcompat:appcompat:1.3.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"

        //URL: https://developer.android.com/jetpack/androidx/releases/core
        const val coreKtx = "androidx.core:core-ktx:1.5.0"

        //URL: https://developer.android.com/jetpack/androidx/releases/work
        internal const val workVersion = "2.6.0-beta01"
        const val workRuntime = "androidx.work:work-runtime-ktx:$workVersion"

        const val biometrics = "androidx.biometric:biometric:1.1.0"

        object Compose {
            //URL: https://developer.android.com/jetpack/androidx/releases/compose
            const val version = "1.0.3"

            const val ui = "androidx.compose.ui:ui:$version"
            const val runtime = "androidx.compose.runtime:runtime:$version"
            const val foundation = "androidx.compose.foundation:foundation:$version"
            const val activity = "androidx.activity:activity-compose:1.3.0-alpha07"
            const val animation = "androidx.compose.animation:animation:$version"
            const val material = "androidx.compose.material:material:$version"
            const val materialIconsExtended =
                "androidx.compose.material:material-icons-extended:$version"
            const val livedata = "androidx.compose.runtime:runtime-livedata:$version"
            const val tooling = "androidx.compose.ui:ui-tooling:$version"


            //URL: https://developer.android.com/jetpack/androidx/releases/lifecycle
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha05"
        }

        object Lifecycle {
            //URL: https://developer.android.com/jetpack/androidx/releases/lifecycle
            private const val version = "2.3.1"

            const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val savedState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:$version"
            const val lifecycleScope = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val annotationProcessor = "androidx.lifecycle:lifecycle-compiler:$version"
        }

        object Reorder {
            //URL: https://developer.android.com/jetpack/androidx/releases/recyclerview
            const val recyclerView = "androidx.recyclerview:recyclerview:1.2.0"
        }

        object Other {
            const val webView = "androidx.webkit:webkit:1.4.0"
        }
    }

    object Google {
        //URL: https://developers.google.com/android/guides/google-services-plugin
        const val playServicesPlugin = "com.google.gms:google-services:4.3.10"

        //URL: https://mvnrepository.com/artifact/com.google.android.gms/play-services-auth
        const val googleAuth = "com.google.android.gms:play-services-auth:19.2.0"

        const val billing = "com.android.billingclient:billing-ktx:4.0.0"

        //In-App Reviews SDK
        const val playCore = "com.google.android.play:core:1.10.0"
        const val playCoreKtx = "com.google.android.play:core-ktx:1.8.1"

        object Firebase {
            const val crashlyticsPlugin = "com.google.firebase:firebase-crashlytics-gradle:2.4.1"
            const val crashlytics = "com.google.firebase:firebase-crashlytics:17.3.0"

            const val analytics = "com.google.firebase:firebase-analytics:18.0.0"

            const val messaging = "com.google.firebase:firebase-messaging:21.0.0"
        }

        object Accompanist {
            //URL: https://github.com/google/accompanist
            private const val version = "0.15.0"
            const val coil = "com.google.accompanist:accompanist-coil:$version"
            const val insets = "com.google.accompanist:accompanist-insets:$version"
        }
    }

    object Hilt {
        //URL: https://developer.android.com/training/dependency-injection/hilt-android
        internal const val version = "2.37"
        private const val versionX = "1.0.0-alpha03"

        const val hiltPlugin = "com.google.dagger:hilt-android-gradle-plugin:$version"
        const val hiltAndroid = "com.google.dagger:hilt-android:$version"
        const val hiltDaggerCompiler = "com.google.dagger:hilt-android-compiler:$version"


        //URL: https://mvnrepository.com/artifact/androidx.hilt/hilt-lifecycle-viewmodel?repo=google
        const val hiltViewmodel = "androidx.hilt:hilt-lifecycle-viewmodel:$versionX"
        const val hiltCompiler = "androidx.hilt:hilt-compiler:$versionX"

        //URL: https://developer.android.com/training/dependency-injection/hilt-jetpack#workmanager
        const val hiltWorkManager = "androidx.hilt:hilt-work:$versionX"
    }

    object Room {
        //URL: https://developer.android.com/jetpack/androidx/releases/room
        private const val version = "2.4.0-alpha03"

        const val roomRuntime = "androidx.room:room-runtime:$version"
        const val roomCompiler = "androidx.room:room-compiler:$version"
        const val roomKtx = "androidx.room:room-ktx:$version"
    }

    object Network {
        //URL: https://github.com/square/retrofit
        private const val retrofitVersion = "2.9.0"

        const val retrofit = "com.squareup.retrofit2:retrofit:$retrofitVersion"
        const val retrofitGsonConverter = "com.squareup.retrofit2:converter-gson:$retrofitVersion"

        //URL: https://github.com/google/gson
        const val gson = "com.google.code.gson:gson:2.8.7"

        //URL: https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor
        const val okhttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:4.9.1"
    }

    object Coroutines {
        //URL: https://github.com/Kotlin/kotlinx.coroutines
        private const val version = "1.5.0"

        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"

        //URL: https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-play-services
        const val playServices = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.4.3"
    }

    object ThirdPartyUI {
        //URL: https://github.com/airbnb/lottie-android
        const val lottie = "com.airbnb.android:lottie:3.7.0"

        //URL: https://github.com/jeziellago/compose-markdown
        const val markdownText = "com.github.jeziellago:compose-markdown:0.2.6"
    }

    object ThirdParty {
        //URL: https://github.com/JakeWharton/timber/releases
        const val timber = "com.jakewharton.timber:timber:4.7.1"

        //URL: https://github.com/greenrobot/EventBus/releases
        const val eventBus = "org.greenrobot:eventbus:3.2.0"

        //URL: https://github.com/notKamui/Keval - evaluate math expressions (calculator)
        const val kval = "com.notkamui.libs:keval:0.7.5"
    }

    object Java {
        const val openCSV = "com.opencsv:opencsv:5.5"
        const val escapeCSVString = "org.apache.commons:commons-lang3:3.12.0"
    }

    object Testing {
        const val junit = "junit:junit:4.13.1"
        const val junitExt = "androidx.test.ext:junit:1.1.2"
        const val espresso = "androidx.test.espresso:espresso-core:3.3.0"
        const val hilt = "com.google.dagger:hilt-android-testing:${Hilt.version}"
        const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Hilt.version}"
        const val work = "androidx.work:work-testing:${AndroidX.workVersion}"
        const val assertK = "com.willowtreeapps.assertk:assertk-jvm:0.23"
        const val architectureComponents = "android.arch.core:core-testing:1.1.1"
        const val mockkAndroid = "io.mockk:mockk-android:1.10.0"
        const val mockk = "io.mockk:mockk:1.10.0"
    }
}
