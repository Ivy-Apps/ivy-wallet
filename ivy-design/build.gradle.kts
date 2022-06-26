import com.ivy.buildsrc.*

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = com.ivy.buildsrc.Project.compileSdkVersion

    defaultConfig {
        minSdk = com.ivy.buildsrc.Project.minSdk
        targetSdk = com.ivy.buildsrc.Project.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = com.ivy.buildsrc.Versions.compose
    }

    packagingOptions {
        //Exclude this files so Jetpack Compose UI tests can build
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
        //-------------------------------------------------------
    }
}

dependencies {
    Kotlin()
    Coroutines(version = "1.5.0")
    FunctionalProgramming(
        arrowVersion = "1.0.1",
        kotestVersion = "5.1.0",
    )

    Compose()

    AndroidX()
    Lifecycle(version = "2.3.1")
}