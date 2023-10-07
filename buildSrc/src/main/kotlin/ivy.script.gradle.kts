plugins {
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    sourceSets.all {
        kotlin.srcDir("build/generated/ksp/$name/kotlin")
    }
}

dependencies {
    implementation(libs.bundles.arrow)
    implementation(libs.bundles.kotlin)

    implementation(catalog.library("kotlinx-serialization-json"))
    testImplementation(libs.bundles.testing)
}