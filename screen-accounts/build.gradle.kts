plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.accounts"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.ivyDesign)
    implementation(projects.ivyNavigation)
    implementation(projects.tempLegacyCode)

    implementation(libs.eventbus)
}