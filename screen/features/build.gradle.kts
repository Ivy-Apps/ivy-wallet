plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.features"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    implementation(projects.shared.ui.core)
    implementation(projects.shared.ui.core)
    implementation(projects.shared.ui.navigation)
}