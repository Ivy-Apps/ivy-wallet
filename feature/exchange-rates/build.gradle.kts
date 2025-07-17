plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.exchangerates"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.data.core)
    implementation(projects.shared.domain)
    implementation(projects.shared.ui.core)
    implementation(projects.shared.ui.navigation)
    implementation(projects.temp.legacyCode)
    implementation(projects.temp.oldDesign)

    testImplementation(projects.shared.ui.testing)
}
