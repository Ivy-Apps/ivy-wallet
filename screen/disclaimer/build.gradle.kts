plugins {
    id("ivy.feature")
    id("com.android.compose.screenshot")
}

android {
    namespace = "com.ivy.disclaimer"
    testOptions {
        screenshotTests {
            imageDifferenceThreshold = BuildConfigConstants.IMAGE_DIFFERENCE_THRESHOLD
        }
    }
}

dependencies {
    implementation(projects.shared.data.core)
    implementation(projects.shared.ui.core)
    implementation(projects.shared.ui.navigation)
}
