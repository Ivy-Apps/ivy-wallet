plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.core.ui"
}

dependencies {
    implementation(projects.ivyResources)
    implementation(projects.tempOldDesign)
    implementation(projects.ivyCore)
    implementation(projects.ivyNavigation)
}