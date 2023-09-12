plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.transaction"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.ivyDesign)
    implementation(projects.ivyNavigation)
    implementation(projects.ivyLegacyCode)
    implementation(projects.widgetBalance)

    implementation(libs.eventbus)
}