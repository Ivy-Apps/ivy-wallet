plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.importdata"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyDomain)
    implementation(projects.ivyResources)
    implementation(projects.tempOldDesign)
    implementation(projects.ivyNavigation)
    implementation(projects.tempLegacyCode)
    implementation(projects.ivyPersistence)

    implementation(projects.screenOnboarding) // TODO: Fix that

    implementation(libs.bundles.opencsv)
}