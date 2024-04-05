plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.importdata"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    
    implementation(projects.temp.oldDesign)
    
    implementation(projects.temp.legacyCode)
    implementation(projects.shared.data)

    implementation(projects.screen.onboarding) // TODO: Fix that

    implementation(libs.bundles.opencsv)

    testImplementation(projects.shared.testing)
}
