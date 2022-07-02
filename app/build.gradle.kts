import com.ivy.buildsrc.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = com.ivy.buildsrc.Project.compileSdkVersion

    defaultConfig {
        applicationId = com.ivy.buildsrc.Project.applicationId
        minSdk = com.ivy.buildsrc.Project.minSdk
        targetSdk = com.ivy.buildsrc.Project.targetSdk
        versionCode = com.ivy.buildsrc.Project.versionCode
        versionName = com.ivy.buildsrc.Project.versionName

        testInstrumentationRunner = "com.ivy.wallet.HiltTestRunner"

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("../debug.jks")
            storePassword = "IVY7834!DEbug"
            keyAlias = "debug"
            keyPassword = "IVY7834!DEbug"
        }

        create("release") {
            storeFile = file("../sign.jks")
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            isDefault = false

            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            resValue("string", "app_name", "Ivy Wallet")
        }

        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isDefault = true

            signingConfig = signingConfigs.getByName("debug")

            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "Ivy Wallet Debug")
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

    lint {
//        isCheckReleaseBuilds = true
//        isAbortOnError = false
    }

    packagingOptions {
        //Exclude this files so Jetpack Compose UI tests can build
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
        //-------------------------------------------------------
    }

    testOptions {
        unitTests.all {
            //Required by Kotest
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":ivy-design"))
    implementation(project(":screens"))
    implementation(project(":account-transactions"))
    implementation(project(":accounts"))
    implementation(project(":budgets"))
    implementation(project(":categories"))
    implementation(project(":category-transactions"))
    implementation(project(":home"))
    implementation(project(":loans"))
    implementation(project(":onboarding"))
    implementation(project(":pie-charts"))
    implementation(project(":planned-payments"))
    implementation(project(":planned-payment-details"))
    implementation(project(":reports"))
    implementation(project(":settings"))
    implementation(project(":search-transactions"))
    implementation(project(":transaction-details"))

    Hilt()

    Google()
    Firebase()

    RoomDB()

    Networking(retrofitVersion = "2.9.0")

    DataStore()

    ThirdParty()
}
