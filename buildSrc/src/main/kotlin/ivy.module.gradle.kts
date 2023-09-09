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

// TODO: Remove
kapt {
    correctErrorTypes = true
    useBuildCache = true
}
