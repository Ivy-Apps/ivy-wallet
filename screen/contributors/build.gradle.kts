plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.contributors"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    implementation(projects.shared.resources)
    implementation(projects.shared.designSystem)
    implementation(projects.shared.navigation)
    implementation(projects.shared.commonUi)

    implementation(libs.bundles.ktor)

    testImplementation(projects.shared.testing)
}
