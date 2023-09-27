plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.features"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyDomain)
    implementation(projects.ivyResources)
    implementation(projects.ivyNavigation)
    implementation(projects.ivyDomainUi)
    implementation(projects.ivyDesign)
}