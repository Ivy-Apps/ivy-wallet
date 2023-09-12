plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.settings"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.ivyDesign)
    implementation(projects.ivyNavigation)
    implementation(projects.ivyLegacyCode)
    implementation(projects.widgetBalance)
}