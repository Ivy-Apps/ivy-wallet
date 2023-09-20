plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.testing"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.ivyDesign)
    implementation(projects.ivyNavigation)
    implementation(projects.ivyCoreUi)
}