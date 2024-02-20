plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.accounts"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    implementation(projects.shared.resources)
    implementation(projects.shared.navigation)
    implementation(projects.temp.oldDesign)
    implementation(projects.temp.legacyCode)
    implementation(projects.shared.data)
    implementation(projects.shared.commonUi)

    testImplementation(projects.shared.testing)
}
