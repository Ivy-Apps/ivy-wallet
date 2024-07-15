plugins {
    id("ivy.feature")
    alias(libs.plugins.screenshot)
}

android {
    namespace = "com.ivy.importdata"
    experimentalProperties["android.experimental.enableScreenshotTest"] = true
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
    screenshotTestImplementation(projects.shared.ui.testing)
}