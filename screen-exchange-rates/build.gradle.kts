plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.exchangerates"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.tempOldDesign)
    implementation(projects.ivyNavigation)
    implementation(projects.tempLegacyCode)
}