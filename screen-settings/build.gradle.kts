plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.settings"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyDomain)
    implementation(projects.ivyResources)
    implementation(projects.tempOldDesign)
    implementation(projects.ivyNavigation)
    implementation(projects.tempLegacyCode)
    implementation(projects.widgetBalance)
    implementation(projects.ivyData)

    testImplementation(projects.ivyTesting)
}