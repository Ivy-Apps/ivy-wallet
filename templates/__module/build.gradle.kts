plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.__module"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.ivyDesign)
    implementation(projects.ivyNavigation)
    implementation(projects.ivyCoreUi)
}