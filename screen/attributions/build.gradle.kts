plugins {
    id("ivy.feature")
    id("com.android.compose.screenshot")
}

android {
    namespace = "com.ivy.attributions"
    testOptions {
        screenshotTests {
            imageDifferenceThreshold = BuildConfigConstants.IMAGE_DIFFERENCE_THRESHOLD
        }
    }
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    implementation(projects.shared.ui.core)
    implementation(projects.shared.ui.navigation)
}
