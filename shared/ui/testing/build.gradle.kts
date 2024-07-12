plugins {
    id("ivy.feature")
    alias(libs.plugins.screenshot)
}

android {
    namespace = "com.ivy.ui.testing"
    experimentalProperties["android.experimental.enableScreenshotTest"] = true
}

dependencies {
    implementation(projects.shared.ui.core)

    // for this module we need test deps as "implementation" and not only "testImplementation"
    // because it'll be added as "testImplementation"
    implementation(libs.bundles.testing)
    implementation(libs.paparazzi)
    screenshotTestImplementation(libs.androidx.compose.ui.tooling)
}
