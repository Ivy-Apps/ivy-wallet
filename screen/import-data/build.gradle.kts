plugins {
    id("ivy.feature")
    id("com.android.compose.screenshot")
}

android {
    namespace = "com.ivy.importdata"
    testOptions {
        screenshotTests {
            imageDifferenceThreshold = BuildConfigConstants.IMAGE_DIFFERENCE_THRESHOLD
        }
    }
}

dependencies {
    implementation(projects.screen.onboarding)
    implementation(projects.shared.base)
    implementation(projects.shared.data.core)
    implementation(projects.shared.domain)
    implementation(projects.shared.ui.core)
    implementation(projects.shared.ui.navigation)
    implementation(projects.temp.legacyCode)
    implementation(projects.temp.oldDesign)

    implementation(libs.bundles.opencsv)
}