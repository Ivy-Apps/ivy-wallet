plugins {
    id("ivy.module")
}

android {
    namespace = "com.ivy.navigation"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyDesign)
}