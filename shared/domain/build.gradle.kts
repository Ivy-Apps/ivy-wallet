plugins {
    id("ivy.feature")
    id("ivy.integration.testing")
}

android {
    namespace = "com.ivy.domain"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.data)

    implementation(libs.datastore)
    implementation(libs.androidx.test.ext)
    androidTestImplementation("junit:junit:4.12")
}
