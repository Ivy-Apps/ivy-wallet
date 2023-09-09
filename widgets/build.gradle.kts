plugins {
    id("ivy.module")
    id("ivy.widget")
}

android {
    namespace = "com.ivy.widgets"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyDesign)
    implementation(projects.ivyResources)
}