plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.accounts"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.tempOldDesign)
    implementation(projects.ivyNavigation)
    implementation(projects.tempLegacyCode)
    implementation(projects.ivyPersistence)
}