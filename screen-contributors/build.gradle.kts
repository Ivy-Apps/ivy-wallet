plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.contributors"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.ivyDesign)
    implementation(projects.ivyNavigation)
    implementation(projects.ivyCoreUi)

    implementation(libs.bundles.ktor)
    implementation(projects.tempLegacyCode)
    implementation(projects.tempOldDesign)
}