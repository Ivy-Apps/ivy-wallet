plugins {
    id("ivy.module")
    org.jetbrains.kotlin.plugin.compose
}

android {
    // Compose
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.bundles.glance)
    implementation(libs.bundles.activity)
}
