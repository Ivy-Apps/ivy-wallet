plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.navigation"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyCore)
    implementation(projects.ivyDesign)
}