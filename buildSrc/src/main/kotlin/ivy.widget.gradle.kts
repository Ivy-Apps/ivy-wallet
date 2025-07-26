plugins {
    id("ivy.module")
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
