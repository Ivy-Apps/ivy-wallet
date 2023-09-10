plugins {
    id("ivy.module")
}

android {
    // Compose
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = catalog.version("compose-compiler")
    }
}

dependencies {
    implementation(libs.bundles.glance)
    implementation(libs.bundles.activity)
}