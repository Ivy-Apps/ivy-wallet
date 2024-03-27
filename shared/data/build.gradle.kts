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

    testImplementation(projects.shared.testing)
    androidTestImplementation(libs.bundles.integration.testing)
}
