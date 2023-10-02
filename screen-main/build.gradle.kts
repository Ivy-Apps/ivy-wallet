plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.main"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyDomain)
    implementation(projects.ivyResources)
    implementation(projects.ivyDesign)
    implementation(projects.ivyNavigation)
    implementation(projects.ivyCommonUi)
    implementation(projects.ivyData)

    implementation(projects.screenHome)
    implementation(projects.screenAccounts)

    implementation(projects.tempOldDesign)
    implementation(projects.tempLegacyCode)

    testImplementation(projects.ivyTesting)
}