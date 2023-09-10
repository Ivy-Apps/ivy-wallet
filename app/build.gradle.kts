plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")

    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.ivy.wallet"
    compileSdk = libs.versions.compile.sdk.get().toInt()

    // TODO: Remove after migrating to KSP
    kapt {
        correctErrorTypes = true
        useBuildCache = true
    }

    defaultConfig {
        applicationId = "com.ivy.wallet"
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.compile.sdk.get().toInt()
        versionName = libs.versions.version.name.get()
        versionCode = libs.versions.version.code.get().toInt()
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            isDebuggable = false
            isDefault = false

            signingConfig = signingConfigs.getByName("release")

            resValue("string", "app_name", "Ivy Wallet")
        }

        debug {
            isMinifyEnabled = false
            isShrinkResources = false

            isDebuggable = true
            isDefault = true

            signingConfig = signingConfigs.getByName("debug")

            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "Ivy Wallet Debug")
        }

        create("demo") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            matchingFallbacks.add("release")
            matchingFallbacks.add("debug")

            isDebuggable = true
            isDefault = false

            signingConfig = signingConfigs.getByName("debug")

            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "Ivy Wallet Demo")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    testOptions {
        unitTests.all {
            // Required by Kotest
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(projects.ivyDesign)
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.ivyNavigation)

    implementation(libs.ivy.frp.temp)
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.arrow)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.glance)
    implementation(libs.bundles.activity)
    implementation(libs.bundles.google)
    implementation(libs.bundles.firebase)
    implementation(libs.datastore)
    implementation(libs.androidx.security)
    implementation(libs.androidx.biometrics)

    implementation(libs.gson)

    implementation(libs.bundles.hilt)
    kapt(libs.hilt.compiler)

    implementation(libs.bundles.room)
    kapt(libs.room.compiler)

    implementation(libs.timber)
    implementation(libs.eventbus)
    implementation(libs.keval)
    implementation(libs.bundles.opencsv)
    implementation(libs.androidx.work)
    implementation(libs.androidx.recyclerview)

    testImplementation(libs.bundles.kotest)
    testImplementation(libs.bundles.kotlin.test)
    testImplementation(libs.hilt.testing)
    testImplementation(libs.androidx.work.testing)
}

// TODO: Remove after migrating to KSP
kapt {
    correctErrorTypes = true
    useBuildCache = true
}