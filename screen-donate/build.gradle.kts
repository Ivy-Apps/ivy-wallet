plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.donate"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.ivyDesign)
    implementation(projects.ivyNavigation)
}