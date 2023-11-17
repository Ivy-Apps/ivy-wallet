plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.planned"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyDomain)
    implementation(projects.ivyResources)
    implementation(projects.tempOldDesign)
    implementation(projects.ivyNavigation)
    implementation(projects.tempLegacyCode)
    implementation(projects.ivyData)

    testImplementation(projects.ivyTesting)
}