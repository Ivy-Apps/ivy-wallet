plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.import-data"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.ivyDesign)
    implementation(projects.ivyNavigation)
}