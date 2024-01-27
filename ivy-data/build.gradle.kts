plugins {
    id("ivy.feature")
    id("ivy.room")
}

android {
    namespace = "com.ivy.data"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(projects.ivyBase)

    implementation(libs.datastore)
    implementation(libs.bundles.ktor)

    androidTestImplementation(libs.bundles.integration.testing)
    testImplementation(projects.ivyTesting)
}
