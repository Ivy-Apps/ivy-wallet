plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.main"
}

dependencies {
    implementation(projects.feature.accounts)
    implementation(projects.feature.home)
    implementation(projects.shared.base)
    implementation(projects.shared.data.core)
    implementation(projects.shared.domain)
    implementation(projects.shared.ui.core)
    implementation(projects.shared.ui.navigation)
    implementation(projects.temp.legacyCode)
    implementation(projects.temp.oldDesign)
}
