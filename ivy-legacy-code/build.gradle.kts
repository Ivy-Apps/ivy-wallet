plugins {
    id("ivy.feature")
    id("ivy.room")
}

android {
    namespace = "com.ivy.legacy"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.ivyDesign)
    implementation(projects.ivyNavigation)
    implementation(libs.androidx.work)
}