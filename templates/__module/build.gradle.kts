plugins {
    id("ivy.module")
}

android {
    namespace = "com.ivy.__module"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
}