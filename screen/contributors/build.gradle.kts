plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.contributors"
}

dependencies {
    implementation(libs.bundles.ktor)
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    implementation(projects.shared.ui.core)
    implementation(projects.shared.ui.navigation)

    testImplementation(projects.shared.testing)
}
