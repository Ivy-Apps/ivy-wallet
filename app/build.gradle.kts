import com.ivy.buildsrc.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("io.kotest")
}

android {
    compileSdk = com.ivy.buildsrc.Project.compileSdkVersion

    defaultConfig {
        applicationId = com.ivy.buildsrc.Project.applicationId
        minSdk = com.ivy.buildsrc.Project.minSdk
        targetSdk = com.ivy.buildsrc.Project.targetSdk
        versionCode = com.ivy.buildsrc.Project.versionCode
        versionName = com.ivy.buildsrc.Project.versionName

        testInstrumentationRunner = "com.ivy.wallet.IvyAppTestRunner"

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

        create("demo") {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            isDefault = false

            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            applicationIdSuffix = ".debug"
            matchingFallbacks.add("release")
            resValue("string", "app_name", "Ivy Wallet Demo")
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
        checkDependencies = true
        xmlReport = false
        htmlReport = true
        htmlOutput = File(projectDir, "lint-merged-report.html")
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
    implementation(project(":common:main"))
    implementation(project(":design-system"))
    implementation(project(":core:ui"))
    implementation(project(":navigation"))
    implementation(project(":categories"))
    implementation(project(":settings"))
    implementation(project(":transaction"))
    implementation(project(":core:data-model"))
    implementation(project(":widgets"))
    implementation(project(":main:impl"))
    implementation(project(":app-locked"))
    implementation(project(":billing"))
    implementation(project(":android-notifications"))
    implementation(project(":core:exchange-provider"))
    implementation(project(":core:domain"))
    implementation(project(":debug"))
    implementation(project(":onboarding"))
    Hilt()

    Google()
    Firebase()

    RoomDB(api = false)

    Networking(api = false)
    Testing()

    DataStore(api = false)

    ThirdParty()
}
