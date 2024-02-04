plugins {
    id("ivy.feature")
    id("ivy.room")
    id("ivy.integration.testing")
}

android {
    namespace = "com.ivy.data"
}

dependencies {
    implementation(projects.shared.base)

    implementation(libs.datastore)
    implementation(libs.bundles.ktor)

    androidTestImplementation(libs.bundles.integration.testing)
    androidTestImplementation(projects.shared.testing)
    testImplementation(projects.shared.testing)
}
