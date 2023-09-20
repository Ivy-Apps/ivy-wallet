plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.home"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.tempOldDesign)
    implementation(projects.ivyNavigation)
    implementation(projects.tempLegacyCode)

    implementation(projects.widgetAddTransaction)
}