plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt") // TODO: Remove
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
}

kotlin {
    sourceSets.all {
        kotlin.srcDir("build/generated/ksp/$name/kotlin")
    }
}

android {
    // Kotlin
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    // Android
    compileSdk = catalog.version("compile-sdk").toInt()

    // Compose
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = catalog.version("compose-compiler")
    }

    // Kotest
    testOptions {
        unitTests.all {
            // Required by Kotest
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(libs.bundles.arrow)
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.compose)
    implementation(libs.timber)

    implementation(libs.bundles.hilt)
    // TODO: Migrate to KSP when supported
    kapt(catalog.library("hilt-compiler"))

    testImplementation(libs.bundles.kotest)
    testImplementation(catalog.bundle("kotlin-test"))
    testImplementation(catalog.library("hilt-testing"))
}

// TODO: Remove after migrating to KSP
kapt {
    correctErrorTypes = true
    useBuildCache = true
}