plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.releases"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.ivyDesign)
    implementation(projects.ivyNavigation)
    implementation(projects.ivyCoreUi)

    implementation(libs.bundles.ktor)
}