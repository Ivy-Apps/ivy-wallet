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
    compileSdk = libs.version("compile-sdk").toInt()

    // Compose
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.version("compose-compiler")
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
    implementation(libs.bundle("arrow"))
    implementation(libs.bundle("kotlin"))
    implementation(libs.bundle("compose"))
    implementation(libs.library("timber"))

    implementation(libs.bundle("hilt"))
    // TODO: Migrate to KSP when supported
    kapt(libs.library("hilt-compiler"))


    testImplementation(libs.bundle("kotest"))
    testImplementation(libs.bundle("kotlin-test"))
    testImplementation(libs.library("hilt-testing"))
}

// TODO: Remove after migrating to KSP
kapt {
    correctErrorTypes = true
    useBuildCache = true
}

// Version Catalog workarounds:
internal val Project.libs: VersionCatalog
    get() =
        project.extensions.getByType<VersionCatalogsExtension>().named("libs")

fun VersionCatalog.version(alias: String): String =
    this.findVersion(alias).get().requiredVersion

fun VersionCatalog.bundle(alias: String): Any =
    this.findBundle(alias).get()

fun VersionCatalog.library(alias: String): Any =
    this.findLibrary(alias).get()
