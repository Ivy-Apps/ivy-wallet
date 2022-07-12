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

            correctErrorTypes = true
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
            //TODO: R8 disabled until `modularization` is stable
            isMinifyEnabled = false
            isShrinkResources = false
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
        freeCompilerArgs = freeCompilerArgs + listOf("-Xskip-prerelease-check")
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = com.ivy.buildsrc.Versions.composeCompilerVersion
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

    hilt {
        enableExperimentalClasspathAggregation = true
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
    implementation(project(":ui-common"))
    implementation(project(":app-base"))
    implementation(project(":screens"))
    implementation(project(":budgets"))
    implementation(project(":categories"))
    implementation(project(":loans"))
    implementation(project(":onboarding"))
    implementation(project(":pie-charts"))
    implementation(project(":planned-payments"))
    implementation(project(":reports"))
    implementation(project(":settings"))
    implementation(project(":search-transactions"))
    implementation(project(":transaction-details"))
    implementation(project(":data-model"))
    implementation(project(":ui-components-old"))
    implementation(project(":customer-journey"))
    implementation(project(":widgets"))
    implementation(project(":main"))
    implementation(project(":app-locked"))
    implementation(project(":balance-prediction"))
    implementation(project(":donate"))
    implementation(project(":item-transactions"))
    implementation(project(":web-view"))
    implementation(project(":settings"))
    implementation(project(":import-csv-backup"))

    implementation(project(":temp-domain"))
    implementation(project(":temp-persistence"))
    implementation(project(":temp-network"))
    implementation(project(":billing"))
    implementation(project(":android-notifications"))
    implementation(project(":exchange"))

    Hilt()

    Google()
    Firebase()

    RoomDB(api = false)

    Networking(api = false)

    DataStore(api = false)

    ThirdParty()
}
