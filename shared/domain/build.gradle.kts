plugins {
    id("ivy.feature")
    id("ivy.integration.testing")
    id("ivy.room")
}

android {
    namespace = "com.ivy.domain"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.data)

    implementation(libs.datastore)
    implementation(libs.bundles.ktor)

    androidTestImplementation(libs.bundles.integration.testing)
    androidTestImplementation(libs.mockk.android)
}