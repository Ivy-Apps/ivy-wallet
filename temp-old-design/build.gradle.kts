plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.design"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyResources)
    implementation(projects.ivyDesign)
    implementation(projects.ivyDomain)
}