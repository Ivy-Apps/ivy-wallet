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
    api(projects.shared.data.model)

    api(libs.datastore)
    implementation(libs.bundles.ktor)

    testImplementation(projects.shared.data.modelTesting)
    androidTestImplementation(libs.bundles.integration.testing)
}
