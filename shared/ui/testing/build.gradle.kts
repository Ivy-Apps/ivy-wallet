plugins {
    id("ivy.feature")
    id("com.android.compose.screenshot")
}

android {
    namespace = "com.ivy.ui.testing"
    testOptions {
        screenshotTests {
            imageDifferenceThreshold = 0.0001f
        }
    }
}

dependencies {
    implementation(projects.shared.ui.core)

    // for this module we need test deps as "implementation" and not only "testImplementation"
    // because it'll be added as "testImplementation"
    implementation(libs.bundles.testing)
}
