plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.main"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.ivyDesign)
    implementation(projects.ivyNavigation)
    implementation(projects.ivyCoreUi)
    implementation(projects.ivyPersistence)

    implementation(projects.screenHome)
    implementation(projects.screenAccounts)

    implementation(projects.tempOldDesign)
    implementation(projects.tempLegacyCode)
}