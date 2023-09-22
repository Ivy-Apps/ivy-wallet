plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.attributions"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.ivyNavigation)
    implementation(projects.ivyCoreUi)
    implementation(projects.ivyDesign)
}