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

    val mockkVersion = "1.13.10"
    androidTestImplementation(libs.bundles.integration.testing)
    androidTestImplementation(projects.shared.testing)
    androidTestImplementation("io.mockk:mockk-android:$mockkVersion")
    androidTestImplementation("io.mockk:mockk-agent:$mockkVersion")
}
