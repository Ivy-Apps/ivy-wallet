plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.releases"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    implementation(projects.shared.ui.core)
    implementation(projects.shared.ui.navigation)

    implementation(libs.bundles.ktor)
}
